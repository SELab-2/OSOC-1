package be.osoc.team1.backend.security

import be.osoc.team1.backend.exceptions.InvalidTokenException
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Date
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.random.Random.Default.nextBytes

/**
 * everything needed to create and process a token
 */
object TokenUtil {
    /**
     * nextBytes generates a random ByteArray each time TokenUtil initialises. This ByteArray is used in the hashing
     * algorithm below. 16 bytes or 128 bits is the perfect length for this ByteArray according to:
     * https://security.stackexchange.com/questions/95972/what-are-requirements-for-hmac-secret-key
     */
    private val secret: ByteArray = nextBytes(16)

    /**
     * We use the HMAC-SHA256 algorithm to hash our token, so we can test the token's integrity. The hashing algorithm
     * is configured to use
     */
    private val hashingAlgorithm: Algorithm = Algorithm.HMAC256(secret)

    /**
     * Create a JSON web token. The token contains email, expiration date of token, whether the token is an access token
     * and the role of the user. Set isAccessToken to true when making an access token, set it to false when creating a
     * refresh token. Access tokens are valid for 5 minutes, while refresh tokens stay valid for 12 hours.
     * The created token gets signed using above hashing algorithm and secret.
     */
    private fun createToken(email: String, authorities: List<String>, isAccessToken: Boolean): String {
        val minutesToLive: Int = if (isAccessToken) 5 else 60 * 12
        return JWT.create()
            .withSubject(email)
            .withExpiresAt(Date(System.currentTimeMillis() + minutesToLive * 60 * 1000))
            .withClaim("isAccessToken", isAccessToken)
            .withClaim("authorities", authorities)
            .sign(hashingAlgorithm)
    }

    /**
     * TODOC
     */
    fun getTokenFromRequest(request: HttpServletRequest): String? {
        val authorizationHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            return authorizationHeader.substring("Basic ".length)
        }
        return null
    }

    /**
     * Interpret token and verify its validity
     * Catch the error if the given access token is invalid, and add error to response instead
     */
    fun decodeAndVerifyToken(token: String): DecodedJWT {
        try {
            val verifier: JWTVerifier = JWT.require(hashingAlgorithm).build()
            return verifier.verify(token)
        } catch (exception: Exception) {
            throw InvalidTokenException()
        }
    }

    /**
     * // TODOC
     */
    fun authenticateWithToken(decodedToken: DecodedJWT) {
        val username: String = decodedToken.subject
        val authorities = getAuthoritiesFromToken(decodedToken)
        if (decodedToken.getClaim("isAccessToken").asBoolean()) {
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(username, null, authorities)
        } else {
            throw InvalidTokenException("You cannot authenticate with a refresh token.")
        }
    }

    /**
     * extract the authorities of the logged in user from the token
     * return the authorities as [SimpleGrantedAuthority] as they need to be, to work with [UsernamePasswordAuthenticationToken]
     */
    private fun getAuthoritiesFromToken(decodedToken: DecodedJWT): List<SimpleGrantedAuthority> {
        val authorities: Array<String> = decodedToken.getClaim("authorities").asArray(String::class.java)
        val grantedAuthorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
        authorities.forEach { role -> grantedAuthorities.add(SimpleGrantedAuthority(role)) }
        return grantedAuthorities
    }

    /**
     * TODOC
     */
    fun createAccessAndRefreshToken(
        response: HttpServletResponse,
        email: String,
        authorities: List<String>,
        oldRefreshToken: String? = null
    ) {
        val accessToken: String = createToken(email, authorities, true)
        val refreshToken: String = oldRefreshToken ?: createToken(email, authorities, false)

        val tokens: MutableMap<String, String> = HashMap()
        tokens["accessToken"] = accessToken
        tokens["refreshToken"] = refreshToken

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, tokens)
    }
}
