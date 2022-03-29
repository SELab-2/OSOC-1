package be.osoc.team1.backend.security

import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidTokenException
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import java.util.stream.Collectors
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
     * Create a JSON web token. The token contains username, expiration date and roles of user. In the end, the token
     * gets signed using above hashing algorithm.
     * this function can be used for making an access token or even a refresh token.
     */
    fun createToken(email: String, role: String, minutesToLive: Int): String {
        return JWT.create()
            .withSubject(email)
            .withExpiresAt(Date(System.currentTimeMillis() + minutesToLive * 60 * 1000))
            .withClaim("role", role)
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
     * extract the username and the authorities/roles from the token
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
        val authority = SimpleGrantedAuthority(decodedToken.getClaim("role").asString())
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(username, null, listOf(authority))
    }

    /**
     * extract the roles of the logged in user from the token
     * return the roles as [SimpleGrantedAuthority] as they need to be, to work with [UsernamePasswordAuthenticationToken]
     */
    private fun getAuthoritiesFromToken(decodedToken: DecodedJWT): List<SimpleGrantedAuthority> {
        val roles: Array<String> = decodedToken.getClaim("roles").asArray(String::class.java)
        val authorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
        roles.forEach { role -> authorities.add(SimpleGrantedAuthority(role)) }
        return authorities
    }

    fun createAccessAndRefreshToken(
        response: HttpServletResponse,
        email: String,
        role: String,
        oldRefreshToken: String? = null
    ) {
        val accessToken: String = createToken(email, role, 5)
        val refreshToken: String = oldRefreshToken ?: createToken(email, role, 60 * 24)

        val tokens: MutableMap<String, String> = HashMap()
        tokens["accessToken"] = accessToken
        tokens["refreshToken"] = refreshToken

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, tokens)
    }
}




