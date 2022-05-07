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
     */
    private const val emailAddressSender = "noreply@osoc.com"
    private const val passwordSender = "insert.password.here"

    /**
     * Make the body of the email users receive when they request a password change.
     */
    private fun getResetPasswordEmailBody(resetPasswordUUID: UUID): String {
        val url = "http://localhost:3000/resetPassword/$resetPasswordUUID"
        return """
            Hi,
            
            Trouble signing in?
            Resetting your password is easy.
            Use the link below to choose your new password.
            $url
            
            If you did not forget your password, please disregard this email.
        """.trimIndent()
    }

    /**
     * Get a [JavaMailSender] object which is correctly configured.
     */
    private fun getMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "smtp.gmail.com"
        mailSender.port = 587
        mailSender.username = emailAddressSender
        mailSender.password = passwordSender
        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"
        return mailSender
    }

    /**
     * Email [emailAddressReceiver] with a [resetPasswordUUID], so [emailAddressReceiver] can reset its email.
     */
    fun sendEmail(emailAddressReceiver: String, resetPasswordUUID: UUID) {
        // val mailSender = getMailSender()

        val email = SimpleMailMessage()
        email.setSubject("Reset Password")
        email.setText(getResetPasswordEmailBody(resetPasswordUUID))
        email.setTo(emailAddressReceiver)
        email.setFrom(emailAddressSender)

        println(">>>>>>>")
        println("To: $emailAddressReceiver")
        println("From: $emailAddressSender")
        println("> ${email.subject}")
        println(email.text)
        println(">>>>>>>")
        // mailSender.send(email)
    }
}
