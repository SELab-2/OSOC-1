package be.osoc.team1.backend.security

import java.security.MessageDigest
import java.util.UUID

object ResetPasswordUtil {
    private val resetTokens: MutableMap<ByteArray, ResetToken> = mutableMapOf()

    private val sha256: MessageDigest = MessageDigest.getInstance("SHA256")

    private fun hash(uuid: UUID): ByteArray {
        return sha256.digest(uuid.toString().toByteArray())
    }

    fun newToken(email: String): UUID {
        val uuid: UUID = UUID.randomUUID()
        val hashedUUID: ByteArray = hash(uuid)
        resetTokens[hashedUUID] = ResetToken(email)
        return uuid
    }

    private fun isTokenValid(hashedUUID: ByteArray): Boolean {
        return (hashedUUID in resetTokens && !resetTokens[hashedUUID]!!.isExpired())
    }

    fun getEmailFromUUID(uuid: UUID): String? {
        val hashedUUID = hash(uuid)
        if (isTokenValid(hashedUUID)) {
            return resetTokens[hashedUUID]!!.email
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
