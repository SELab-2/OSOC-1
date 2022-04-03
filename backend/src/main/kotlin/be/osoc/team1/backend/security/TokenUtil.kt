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
import kotlin.random.Random.Default.nextInt

/**
 * This object contains every function needed to create and process a token (works with both access and refresh tokens).
 */
object TokenUtil {
    /**
     * nextBytes generates a random ByteArray. This ByteArray is used in the hashing algorithm below. The perfect length
     * for this ByteArray is 16 bytes or 128 bits according to:
     * https://security.stackexchange.com/questions/95972/what-are-requirements-for-hmac-secret-key
     */
    private val secret: ByteArray = nextBytes(16)

    /**
     * We use the HMAC-SHA256 algorithm to sign/hash our token, this is done to test the token's integrity.
     */
    private val hashingAlgorithm: Algorithm = Algorithm.HMAC256(secret)

    /**
     * This map holds the latest refresh token per email, so the keys are emails and the values are refresh tokens. This
     * map is essential for [refreshTokenRotation].
     */
    private val validRefreshTokens: MutableMap<String, String> = mutableMapOf()

    /**
     * Create a JSON web token. The token contains email, an id, expiration date of token, whether the token is an
     * access token and the authorities of the user. Set [isAccessToken] to true when making an access token, set it to
     * false when creating a refresh token. Access tokens are valid for 5 minutes, while refresh tokens stay valid for
     * 12 hours.
     * The created token gets signed using above hashing algorithm and secret.
     */
    private fun createToken(
        email: String,
        authorities: List<String>,
        isAccessToken: Boolean,
        expirationDate: Date? = null
    ): String {
        val minutesToLive: Int = if (isAccessToken) 5 else 60 * 12
        return JWT.create()
            .withSubject(email)
            .withJWTId(nextInt().toString())
            .withExpiresAt(
                expirationDate ?: Date(System.currentTimeMillis() + minutesToLive * 60 * 1000)
            )
            .withClaim("isAccessToken", isAccessToken)
            .withClaim("authorities", authorities)
            .sign(hashingAlgorithm)
    }

    /**
     * Extract access token from request header. return null when there is no access token given.
     */
    fun getAccessTokenFromRequest(request: HttpServletRequest): String? {
        val authorizationHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            return authorizationHeader.substring("Basic ".length)
        }
        return null
    }

    /**
     * Interpret token and verify its validity and integrity. Throw an [InvalidTokenException] when the given token is
     * invalid.
     */
    fun decodeAndVerifyToken(token: String): DecodedJWT {
        try {
            val verifier: JWTVerifier = JWT.require(hashingAlgorithm).build()
            return verifier.verify(token)
        } catch (exception: Exception) {
            throw InvalidTokenException("Token is signed incorrectly, integrity of token could not be verified.")
        }
    }

    /**
     * Interpret access token and authenticate the user with it. Throw an [InvalidTokenException] when a refresh token
     * is given.
     */
    fun authenticateWithAccessToken(decodedToken: DecodedJWT) {
        if (!decodedToken.getClaim("isAccessToken").asBoolean()) {
            throw InvalidTokenException("You cannot authenticate with a refresh token.")
        }
        val username: String = decodedToken.subject
        val authorities = getAuthoritiesFromToken(decodedToken)
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(username, null, authorities)
    }

    /**
     * Extract the authorities of the logged-in user from the token. Return the authorities as [SimpleGrantedAuthority]
     * as they need to be, to work with [UsernamePasswordAuthenticationToken].
     */
    private fun getAuthoritiesFromToken(decodedToken: DecodedJWT): List<SimpleGrantedAuthority> {
        val authorities: Array<String> = decodedToken.getClaim("authorities").asArray(String::class.java)
        val grantedAuthorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
        authorities.forEach { role -> grantedAuthorities.add(SimpleGrantedAuthority(role)) }
        return grantedAuthorities
    }

    /**
     * Create an access and refresh token and add these tokens to the [response]. When this function gets called to
     * renew an access token, only create a new access token and keep the old refresh token.
     */
    fun createAccessAndRefreshToken(
        response: HttpServletResponse,
        email: String,
        authorities: List<String>,
        refreshTokenExpiresAt: Date? = null
    ) {
        val accessToken: String = createToken(email, authorities, true)
        val refreshToken: String = createToken(email, authorities, false, refreshTokenExpiresAt)
        val tokens: MutableMap<String, String> = HashMap()
        tokens["accessToken"] = accessToken
        tokens["refreshToken"] = refreshToken

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, tokens)

        validRefreshTokens[email] = refreshToken
    }

    /**
     * Refresh token rotation means that every time we request a new access token, we also get a new refresh token. If a
     * refresh token is used more than once, we invalidate all the refresh tokens that user used, and this
     * user has to go through the authentication process again. This is necessary to hinder token theft.
     *
     * Here we use the [validRefreshTokens] map, to make sure users only use their latest refresh token to renew their
     * access token. When a used refresh token gets used for the second time, the email of the user gets removed from
     * [validRefreshTokens]. This means this user needs to re-authenticate to be able to renew their access token.
     *
     * More on refresh token rotation can be read on the following link:
     * https://www.pingidentity.com/en/resources/blog/posts/2021/refresh-token-rotation-spa.html
     */
    fun refreshTokenRotation(
        response: HttpServletResponse,
        refreshToken: String,
        email: String,
        authorities: List<String>,
        expirationDate: Date
    ) {
        if (validRefreshTokens[email] == refreshToken) {
            createAccessAndRefreshToken(response, email, authorities, expirationDate)
        } else {
            validRefreshTokens.remove(email)
            response.status = 400
        }
    }
}
