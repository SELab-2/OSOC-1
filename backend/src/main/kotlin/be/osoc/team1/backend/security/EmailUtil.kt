package be.osoc.team1.backend.security

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

/**
 * This object contains every function needed to create and send emails.
 */
object EmailUtil {
    private fun getResetPasswordEmailBody(
        contextPath: String, token: String
    ): String {
        val url = "$contextPath/users/resetpassword?$token"
        val message: String = "Hallo, \r\n dit is een testtt.tt. \n mvg\r\ntestymen"
        return "$message \r\n$url"
    }

    private fun getJavaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "smtp.gmail.com"
        mailSender.port = 587
        mailSender.username = "my.gmail@gmail.com"
        mailSender.password = "password"
        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"
        return mailSender
    }


    fun sendEmail(emailaddress: String, resetPasswordToken: String) {
        val mailSender = getJavaMailSender()

        val email = SimpleMailMessage()
        email.setSubject("Reset Password")
        email.setText(getResetPasswordEmailBody("/api", resetPasswordToken))
        email.setTo(emailaddress)
        email.setFrom("osoc.support@mail.com")

        mailSender.send(email)
    }
}