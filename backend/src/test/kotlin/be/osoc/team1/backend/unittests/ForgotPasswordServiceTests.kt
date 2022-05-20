package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidForgotPasswordUUIDException
import be.osoc.team1.backend.exceptions.InvalidGmailCredentialsException
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.services.EmailService
import be.osoc.team1.backend.services.ForgotPasswordService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.env.Environment
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Properties
import java.util.UUID

class ForgotPasswordServiceTests {
    private val testEmail = "test@email.com"
    private val testUser = User("Test", testEmail, Role.Admin, "password")
    private val newPassword = "new_password"

    private fun getRepository(userAlreadyExists: Boolean = true): UserRepository {
        val userRepository: UserRepository = mockk()
        every { userRepository.findByEmail(testEmail) } returns if (userAlreadyExists) testUser else null
        every { userRepository.save(testUser) } returns testUser
        return userRepository
    }

    private fun getEnvironment(set: Boolean = true, frontendUrlSet: Boolean = true): Environment {
        val environment: Environment = mockk()
        every { environment.getProperty("OSOC_GMAIL_ADDRESS") } returns if (set) "email@gmail.com" else null
        every { environment.getProperty("OSOC_GMAIL_APP_PASSWORD") } returns if (set) "app_password" else null
        every { environment.getProperty("OSOC_FRONTEND_URL") } returns if (frontendUrlSet) "https://sel2-1.ugent.be" else null
        return environment
    }

    @Test
    fun `sendEmailWithToken does not fail when email is valid`() {
        val mailSender: JavaMailSenderImpl = mockk()
        every { mailSender.host = any() } just Runs
        every { mailSender.port = any() } just Runs
        every { mailSender.username = any() } just Runs
        every { mailSender.password = any() } just Runs
        every { mailSender.javaMailProperties } returns Properties()
        every { mailSender.send(ofType(SimpleMailMessage::class)) } just Runs

        val emailService = EmailService(getEnvironment(), mailSender)
        val forgotPasswordService = ForgotPasswordService(getRepository(), mockk(), emailService)
        forgotPasswordService.sendEmailWithToken(testEmail)
    }

    @Test
    fun `sendEmailWithToken does not fail when email is invalid`() {
        val emailService: EmailService = mockk()
        val forgotPasswordService = ForgotPasswordService(getRepository(false), mockk(), emailService)
        every { emailService.sendEmail(testEmail, any()) } just Runs
        forgotPasswordService.sendEmailWithToken(testEmail)
    }

    @Test
    fun `sendEmailWithToken fails when environment variables aren't set`() {
        val emailService = EmailService(getEnvironment(false), JavaMailSenderImpl())
        val forgotPasswordService = ForgotPasswordService(getRepository(), mockk(), emailService)
        val exception = Assertions.assertThrows(InvalidGmailCredentialsException().javaClass) {
            forgotPasswordService.sendEmailWithToken(testEmail)
        }
        Assertions.assertTrue(exception.message?.startsWith("No 'OSOC_GMAIL_ADDRESS' or") ?: false)
    }

    @Test
    fun `sendEmailWithToken fails when environment variables aren't set correctly`() {
        val emailService = EmailService(getEnvironment(), JavaMailSenderImpl())
        val forgotPasswordService = ForgotPasswordService(getRepository(), mockk(), emailService)
        val exception = assertThrows<InvalidGmailCredentialsException> {
            forgotPasswordService.sendEmailWithToken(testEmail)
        }
        Assertions.assertTrue(exception.message?.startsWith("Make sure 'OSOC_GMAIL_ADDRESS' and") ?: false)
    }

    @Test
    fun `changePassword fails when forgotPasswordUUID is invalid`() {
        val invalidUUID = UUID.randomUUID()
        val forgotPasswordService = ForgotPasswordService(getRepository(), mockk(), mockk())
        val exception = Assertions.assertThrows(InvalidForgotPasswordUUIDException().javaClass) {
            forgotPasswordService.changePassword(invalidUUID, newPassword)
        }
        Assertions.assertEquals("forgotPasswordUUID is invalid.", exception.message)
    }

    @Test
    fun `changePassword fails when email is invalid`() {
        val forgotPasswordService = ForgotPasswordService(getRepository(false), mockk(), mockk())
        val validUUID = forgotPasswordService.newToken(testEmail)
        val exception = Assertions.assertThrows(InvalidForgotPasswordUUIDException().javaClass) {
            forgotPasswordService.changePassword(validUUID, newPassword)
        }
        Assertions.assertEquals("ForgotPasswordToken contains invalid email.", exception.message)
    }

    @Test
    fun `changePassword does not fail when valid arguments given`() {
        val passwordEncoder: PasswordEncoder = mockk()
        every { passwordEncoder.encode(any()) } returns "Encoded password"
        val forgotPasswordService = ForgotPasswordService(getRepository(), passwordEncoder, mockk())
        val validUUID = forgotPasswordService.newToken(testEmail)
        forgotPasswordService.changePassword(validUUID, newPassword)
    }
}
