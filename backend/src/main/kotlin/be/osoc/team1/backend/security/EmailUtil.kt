package be.osoc.team1.backend.security

import be.osoc.team1.backend.exceptions.InvalidGmailCredentialsException
import org.springframework.mail.MailAuthenticationException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties
import java.util.UUID

/**
 * This object contains every function needed to make and send emails.
 */
object EmailUtil {
    /**
     * Credentials of gmail account to send emails with.
     */
    var emailAddressSender: String? = System.getenv("OSOC_GMAIL_ADDRESS")
    var passwordSender: String? = System.getenv("OSOC_GMAIL_APP_PASSWORD")

    /**
     * Make the body of the email users receive when they request a password change.
     */
    private fun getForgotPasswordEmailBody(forgotPasswordUUID: UUID): String {
        val url = "https://sel2-1.ugent.be/forgotPassword/$forgotPasswordUUID"
        return """
            Hi,
            
            Trouble signing in? Resetting your password is easy.
            Use the link below to choose a new password.
            You can only use this link once to reset your password and it is only valid for 20 minutes.
            $url
            (if this link isn't clickable, you can copy and paste it into the search bar)
            
            If you did not forget your password, please disregard this email.
        """.trimIndent()
    }

    /**
     * Get a [JavaMailSender] object which is correctly configured.
     */
    private fun getMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl().apply {
            host = "smtp.gmail.com"
            port = 587
            username = emailAddressSender
            password = passwordSender
        }
        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"
        return mailSender
    }

    /**
     * Email [emailAddressReceiver] with a [forgotPasswordUUID], so [emailAddressReceiver] can reset its email.
     */
    fun sendEmail(emailAddressReceiver: String, forgotPasswordUUID: UUID) {
        println(emailAddressSender)
        if (emailAddressSender == null || passwordSender == null) {
            throw InvalidGmailCredentialsException("No 'OSOC_GMAIL_ADDRESS' or 'OSOC_GMAIL_APP_PASSWORD' found in environment variables.")
        }
        val email = SimpleMailMessage().apply {
            setSubject("Reset Password")
            setText(getForgotPasswordEmailBody(forgotPasswordUUID))
            setTo(emailAddressReceiver)
            setFrom(emailAddressSender!!)
        }
        try {
            println("hunk")
            getMailSender().send(email)
        } catch (_: MailAuthenticationException) {
            throw InvalidGmailCredentialsException(
                "Make sure 'OSOC_GMAIL_ADDRESS' and 'OSOC_GMAIL_APP_PASSWORD' are correctly configured in environment" +
                    "variables. 'OSOC_GMAIL_APP_PASSWORD' should be set to a gmail app password, a normal password won't work here."
            )
        }
    }
}
