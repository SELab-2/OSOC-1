package be.osoc.team1.backend.security

import java.security.MessageDigest
import java.util.SortedMap
import java.util.UUID

/**
 * This object contains every function needed to manage password reset requests.
 */
object ResetPasswordUtil {
    /**
     * This map holds a [ResetPasswordToken] per ...
     */
    private val resetTokens: SortedMap<ByteArray, ResetPasswordToken> = sortedMapOf(
        { a, b -> return@sortedMapOf if (a.contentEquals(b)) 0 else 1 }
    )

    /**
     * Init object that can hash using the SHA-256 hash function.
     */
    private val sha256: MessageDigest = MessageDigest.getInstance("SHA256")

    /**
     * Hash [resetPasswordUUID] with the SHA-256 hash function.
     */
    private fun hash(resetPasswordUUID: UUID): ByteArray {
        return sha256.digest(resetPasswordUUID.toString().toByteArray())
    }

    /**
     * Create a [ResetPasswordToken] for [emailAddress].
     */
    fun newToken(emailAddress: String): UUID {
        val uuid: UUID = UUID.randomUUID()
        val hashedUUID: ByteArray = hash(uuid)
        resetTokens[hashedUUID] = ResetPasswordToken(emailAddress)
        return uuid
    }

    /**
     * Check whether resetPasswordToken linked to [hashedUUID] is valid and hasn't expired yet.
     */
    private fun isTokenValid(hashedUUID: ByteArray): Boolean {
        return (hashedUUID in resetTokens && !resetTokens[hashedUUID]!!.isExpired())
    }

    /**
     * Get which email address requested given [resetPasswordUUID].
     */
    fun getEmailFromUUID(resetPasswordUUID: UUID): String? {
        val hashedUUID = hash(resetPasswordUUID)
        if (isTokenValid(hashedUUID)) {
            return resetTokens[hashedUUID]!!.emailAddress
        }
        return null
    }
}
