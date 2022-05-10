package be.osoc.team1.backend.security

import java.security.MessageDigest
import java.util.SortedMap
import java.util.UUID

/**
 * This object contains every function to manage password forgotten requests.
 */
object ForgotPasswordUtil {
    /**
     * When a user requests to change its password, a random [UUID] gets generated and a new entry gets added to
     * [forgotPasswordTokens]. The key of the entry is the hashed uuid, the value consists of a [ForgotPasswordToken] containing
     * TTL and email of the user. The uuid is used to generate a unique url for the user to change its password.
     */
    private val forgotPasswordTokens: SortedMap<ByteArray, ForgotPasswordToken> = sortedMapOf(
        { a: ByteArray, b: ByteArray -> return@sortedMapOf if (a.contentEquals(b)) 0 else 1 }
    )

    /**
     * Init object that can hash using the SHA-256 hash function.
     */
    private val sha256: MessageDigest = MessageDigest.getInstance("SHA256")

    /**
     * Hash [forgotPasswordUUID] with the SHA-256 hash function.
     */
    private fun hash(forgotPasswordUUID: UUID): ByteArray {
        return sha256.digest(forgotPasswordUUID.toString().toByteArray())
    }

    /**
     * Remove expired tokens from [forgotPasswordTokens].
     */
    private fun removeExpiredTokens() {
        forgotPasswordTokens.values.removeIf { it.isExpired() }
    }

    /**
     * Create a [ForgotPasswordToken] for [emailAddress].
     */
    fun newToken(emailAddress: String): UUID {
        removeExpiredTokens()
        val uuid: UUID = UUID.randomUUID()
        val hashedUUID: ByteArray = hash(uuid)
        forgotPasswordTokens[hashedUUID] = ForgotPasswordToken(emailAddress)
        return uuid
    }

    /**
     * Check whether forgotPasswordToken linked to [hashedUUID] is valid and hasn't expired yet.
     */
    private fun isTokenValid(hashedUUID: ByteArray): Boolean {
        return (hashedUUID in forgotPasswordTokens && !forgotPasswordTokens[hashedUUID]!!.isExpired())
    }

    /**
     * Get which email address requested given [forgotPasswordUUID].
     */
    fun getEmailFromUUID(forgotPasswordUUID: UUID): String? {
        val hashedUUID = hash(forgotPasswordUUID)
        if (isTokenValid(hashedUUID)) {
            return forgotPasswordTokens[hashedUUID]!!.emailAddress
        }
        return null
    }
}
