package be.osoc.team1.backend.security

import java.security.MessageDigest
import java.util.SortedMap
import java.util.UUID

object ResetPasswordUtil {
    private val resetTokens: SortedMap<ByteArray, ResetPasswordToken> = sortedMapOf(
        { a, b -> return@sortedMapOf if (a.contentEquals(b)) 0 else 1 }
    )

    private val sha256: MessageDigest = MessageDigest.getInstance("SHA256")

    private fun hash(uuid: UUID): ByteArray {
        return sha256.digest(uuid.toString().toByteArray())
    }

    fun newToken(emailAddress: String): UUID {
        val uuid: UUID = UUID.randomUUID()
        val hashedUUID: ByteArray = hash(uuid)
        resetTokens[hashedUUID] = ResetPasswordToken(emailAddress)
        return uuid
    }

    private fun isTokenValid(hashedUUID: ByteArray): Boolean {
        return (hashedUUID in resetTokens && !resetTokens[hashedUUID]!!.isExpired())
    }

    fun getEmailFromUUID(uuid: UUID): String? {
        val hashedUUID = hash(uuid)
        if (isTokenValid(hashedUUID)) {
            return resetTokens[hashedUUID]!!.emailAddress
        }
        return null
    }
}
