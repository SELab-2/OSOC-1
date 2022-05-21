package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.exceptions.InvalidGmailCredentialsException
import be.osoc.team1.backend.services.EmailService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.env.Environment
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties
import java.util.UUID

class EmailServiceTests {
    private val testEmail = "test@email.com"
    private val captureSimpleMailMessage = slot<SimpleMailMessage>()

    private fun getEnvironment(emailSet: Boolean = true, passwordSet: Boolean = true): Environment {
        val environment: Environment = mockk()
        every { environment.getProperty("OSOC_GMAIL_ADDRESS") } returns if (emailSet) "email@gmail.com" else null
        every { environment.getProperty("OSOC_GMAIL_APP_PASSWORD") } returns if (passwordSet) "app_password" else null
        every { environment.getProperty("OSOC_FRONTEND_URL") } returns "https://sel2-1.ugent.be"
        return environment
    }

    private fun getMailSender(): JavaMailSenderImpl {
        val mailSender: JavaMailSenderImpl = mockk()
        every { mailSender.host = any() } just Runs
        every { mailSender.port = any() } just Runs
        every { mailSender.username = any() } just Runs
        every { mailSender.password = any() } just Runs
        every { mailSender.javaMailProperties } returns Properties()
        every { mailSender.send(capture(captureSimpleMailMessage)) } just Runs
        return mailSender
    }

    @Test
    fun `sendEmail sends invitation mail when no UUID given`() {
        val mailSender = getMailSender()
        val emailService = EmailService(getEnvironment(), mailSender)
        emailService.sendEmail(testEmail)
        Assertions.assertEquals(
            emailService.invitationMailTitle,
            captureSimpleMailMessage.captured.subject
        )
    }

    @Test
    fun `sendEmail sends forgot password mail when a UUID is given`() {
        val mailSender = getMailSender()
        val emailService = EmailService(getEnvironment(), mailSender)
        emailService.sendEmail(testEmail, UUID.randomUUID())
        Assertions.assertEquals(
            emailService.forgotPasswordMailTitle,
            captureSimpleMailMessage.captured.subject
        )
    }

    @Test
    fun `sendEmail fails when email environment variable isn't set`() {
        val emailService = EmailService(getEnvironment(emailSet = false), JavaMailSenderImpl())
        val exception = Assertions.assertThrows(InvalidGmailCredentialsException().javaClass) {
            emailService.sendEmail(testEmail)
        }
        Assertions.assertTrue(exception.message?.startsWith("No 'OSOC_GMAIL_ADDRESS' or") ?: false)
    }

    @Test
    fun `sendEmail fails when password environment variable isn't set`() {
        val emailService = EmailService(getEnvironment(passwordSet = false), JavaMailSenderImpl())
        val exception = Assertions.assertThrows(InvalidGmailCredentialsException().javaClass) {
            emailService.sendEmail(testEmail)
        }
        Assertions.assertTrue(exception.message?.startsWith("No 'OSOC_GMAIL_ADDRESS' or") ?: false)
    }

    @Test
    fun `sendEmailWithToken fails when environment variables aren't set correctly`() {
        val emailService = EmailService(getEnvironment(), JavaMailSenderImpl())
        val exception = assertThrows<InvalidGmailCredentialsException> {
            emailService.sendEmail(testEmail)
        }
        Assertions.assertTrue(exception.message?.startsWith("Make sure 'OSOC_GMAIL_ADDRESS' and") ?: false)
    }
}
