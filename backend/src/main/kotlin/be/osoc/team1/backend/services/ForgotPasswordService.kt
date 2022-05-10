package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidForgotPasswordUUIDException
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.security.EmailUtil
import be.osoc.team1.backend.security.ForgotPasswordUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ForgotPasswordService(private val repository: UserRepository, private val passwordEncoder: PasswordEncoder) {
    /**
     * Email [emailAddress] a link to reset their password, if [emailAddress] is linked to an existing user.
     */
    fun sendEmailWithToken(emailAddress: String) {
        if (repository.findByEmail(emailAddress) != null) {
            val forgotPasswordUUID: UUID = ForgotPasswordUtil.newToken(emailAddress)
            EmailUtil.sendEmail(emailAddress, forgotPasswordUUID)
        }
    }

    /**
     * Extract the email address from [forgotPasswordUUID] and set the password of that user to [newPassword].
     */
    fun changePassword(forgotPasswordUUID: UUID, newPassword: String) {
        val emailAddress = ForgotPasswordUtil.getEmailFromUUID(forgotPasswordUUID)
            ?: throw InvalidForgotPasswordUUIDException("forgotPasswordUUID is invalid.")
        val user: User = repository.findByEmail(emailAddress)
            ?: throw InvalidForgotPasswordUUIDException("ForgotPasswordToken contains invalid email.")
        user.password = passwordEncoder.encode(newPassword)
        repository.save(user)
    }
}
