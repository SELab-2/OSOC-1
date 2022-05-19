package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidForgotPasswordUUIDException
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.security.EmailUtil
import be.osoc.team1.backend.security.ForgotPasswordToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*

@Service
class ForgotPasswordService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {
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
     * Remove token linked to [forgotPasswordUUID] from [forgotPasswordTokens].
     */
    fun removeToken(forgotPasswordUUID: UUID) {
        forgotPasswordTokens.remove(hash(forgotPasswordUUID))
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

    /**
     * Email [emailAddress] a link to reset their password, if [emailAddress] is linked to an existing user.
     */
    fun sendEmailWithToken(emailAddress: String) {
        if (userRepository.findByEmail(emailAddress) != null) {
            val forgotPasswordUUID: UUID = newToken(emailAddress)
            EmailUtil.sendEmail(emailAddress, forgotPasswordUUID)
        }
    }

    /**
     * Extract the email address from [forgotPasswordUUID] and set the password of that user to [newPassword].
     */
    fun changePassword(forgotPasswordUUID: UUID, newPassword: String) {
        val emailAddress = getEmailFromUUID(forgotPasswordUUID)
            ?: throw InvalidForgotPasswordUUIDException("forgotPasswordUUID is invalid.")
        val user: User = userRepository.findByEmail(emailAddress)
            ?: throw InvalidForgotPasswordUUIDException("ForgotPasswordToken contains invalid email.")
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        removeToken(forgotPasswordUUID)
    }
}
