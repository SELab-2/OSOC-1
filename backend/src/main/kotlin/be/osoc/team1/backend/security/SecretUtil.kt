package be.osoc.team1.backend.security

import com.auth0.jwt.algorithms.Algorithm
import kotlin.random.Random.Default.nextBytes

/**
 * used for token encryption
 * nextBytes generates a random secret each time SecretUtil inits
 * 16 bytes or 128 bits is the perfect length according to
 * https://security.stackexchange.com/questions/95972/what-are-requirements-for-hmac-secret-key
 */
object SecretUtil {
    val algorithm: Algorithm = Algorithm.HMAC256(nextBytes(16))
}
