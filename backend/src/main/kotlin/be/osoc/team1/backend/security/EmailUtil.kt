package be.osoc.team1.backend.security

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
     * Set email account to send emails with.
     * The password below is a Gmail app password, so it can't be used to log into the Google account.
     */
    private const val emailAddressSender = "opensummerofcode.info@gmail.com"
    private const val passwordSender = "nharepxthiwygcpj"

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
        val email = SimpleMailMessage().apply {
            setSubject("Reset Password")
            setText(getForgotPasswordEmailBody(forgotPasswordUUID))
            setTo(emailAddressReceiver)
            setFrom(emailAddressSender)
        }
        getMailSender().send(email)
    }
}
