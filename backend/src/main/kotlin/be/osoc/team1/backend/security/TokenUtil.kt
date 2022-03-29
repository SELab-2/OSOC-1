package be.osoc.team1.backend.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.Date
import java.util.stream.Collectors
import kotlin.random.Random.Default.nextBytes

/**
 * everything needed to create a token
 */
object TokenUtil {
    /**
     * nextBytes generates a random ByteArray each time TokenUtil initialises. This ByteArray is used in the hashing
     * algorithm below.
     * 16 bytes or 128 bits is the perfect length according to
     * https://security.stackexchange.com/questions/95972/what-are-requirements-for-hmac-secret-key
     */
    private val secret: ByteArray = nextBytes(16)

    /**
     * We use the HMAC-SHA256 algorithm to hash our token, so we can test the token's integrity. The hashing algorithm
     * is configured to use
     */
    val hashingAlgorithm: Algorithm = Algorithm.HMAC256(secret)

    /**
     * Create a JSON web token. The token contains username, expiration date and roles of user. In the end, the token
     * gets signed using above hashing algorithm.
     * this function can be used for making an access token or even a refresh token.
     */
    fun createToken(user: User, minutesToLive: Int): String {
        val roles: List<String> =
            user.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
        return JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + minutesToLive * 60 * 1000))
            .withClaim("roles", roles)
            .sign(hashingAlgorithm)
    }
}




