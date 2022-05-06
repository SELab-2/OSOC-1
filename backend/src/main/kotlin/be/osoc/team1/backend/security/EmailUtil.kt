package be.osoc.team1.backend.security

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties
import java.util.UUID

/**
 * This object contains every function needed to create and send emails.
 */
object EmailUtil {
    private const val emailAddressSender = "noreply@osoc.com"
    private const val passwordSender = "insert.password.here"

    private fun getResetPasswordEmailBody(resetPasswordToken: UUID): String {
        val url = "http://localhost:3000/resetPassword/$resetPasswordToken"
        return """
            Hello,
            Use the link below to set your new password.
            $url            
        """.trimIndent()
    }

    private fun getJavaMailSender(): JavaMailSender {
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

    fun sendEmail(emailaddressReceiver: String, resetPasswordToken: UUID) {
        val mailSender = getJavaMailSender()

        val email = SimpleMailMessage()
        email.setSubject("Reset Password")
        email.setText(getResetPasswordEmailBody(resetPasswordToken))
        email.setTo(emailaddressReceiver)
        email.setFrom(emailAddressSender)

        println(">>>>>>>")
        println("To: $emailaddressReceiver")
        println("From: $emailAddressSender")
        println(email.subject)
        println(email.text)
        println(">>>>>>>")
        // mailSender.send(email)
    }
}
