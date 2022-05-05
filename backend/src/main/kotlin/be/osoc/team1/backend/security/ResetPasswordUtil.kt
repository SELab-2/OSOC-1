package be.osoc.team1.backend.security

import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.random.Random

object ResetPasswordUtil {
    private val resetTokens: MutableMap<String, ResetToken> = mutableMapOf()

    fun newToken(email: String, passwordEncoder: PasswordEncoder): String {
        val uuid: String = Random.nextBytes(64).toString()
        val hashedUuid = passwordEncoder.encode(uuid)
        resetTokens[hashedUuid] = ResetToken(email)
        println("uuid: $uuid")
        println("hashed: $hashedUuid")
        return uuid
    }

    private fun isTokenValid(hashedUuid: String): Boolean {
        val resetToken: ResetToken? = resetTokens[hashedUuid]
        return (hashedUuid in resetTokens && !resetTokens[hashedUuid]!!.isExpired())
    }

    fun getEmailFromToken(hashedUuid: String): String? {
        if (isTokenValid(hashedUuid)) {
            return resetTokens[hashedUuid]!!.email
        }
        return null
    }
}

data class ResetToken(
    val email: String,
    val ttl: Long = System.currentTimeMillis() + 30 * 60 * 1000
) {
    fun isExpired(): Boolean {
        return ttl < System.currentTimeMillis()
    }
}
