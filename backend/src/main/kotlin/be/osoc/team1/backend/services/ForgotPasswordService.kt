package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidForgotPasswordUUIDException
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.security.EmailUtil
import be.osoc.team1.backend.security.ResetPasswordUtil
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
            val resetPasswordUUID: UUID = ResetPasswordUtil.newToken(emailAddress)
            EmailUtil.sendEmail(emailAddress, resetPasswordUUID)
        }
    }

    /**
     * Extract the email address from [resetPasswordUUID] and set the password of that user to [newPassword].
     */
    fun changePassword(resetPasswordUUID: UUID, newPassword: String) {
        val emailAddress = ResetPasswordUtil.getEmailFromUUID(resetPasswordUUID)
            ?: throw InvalidForgotPasswordUUIDException("resetPasswordUUID is invalid.")
        val user: User = repository.findByEmail(emailAddress)
            ?: throw InvalidForgotPasswordUUIDException("ResetPasswordToken contains invalid email.")
        user.password = passwordEncoder.encode(newPassword)
        repository.save(user)
    }
}
