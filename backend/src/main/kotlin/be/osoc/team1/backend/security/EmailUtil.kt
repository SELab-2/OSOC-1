package be.osoc.team1.backend.security

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties

/**
 * This object contains every function needed to create and send emails.
 */
object EmailUtil {
    private fun getResetPasswordEmailBody(resetPasswordToken: String): String {
        val url = "api/users/resetPassword?$resetPasswordToken"
        val message: String = "Hallo, \r\n dit is een testtt.tt. \n mvg\r\ntestymen"
        return "$message \r\n$url"
    }

    private fun getJavaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "smtp.gmail.com"
        mailSender.port = 587
        mailSender.username = "tymenvanhimme@gmail.com"
        mailSender.password = "secret"
        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        // props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"
        return mailSender
    }

    fun sendEmail(emailaddress: String, resetPasswordToken: String) {
        println(">>>>>>>")
        println(">>>>>>>")
        println(">>>>>>>")
        println(">>>>>>>")
        println(">>>>>>>")
        println(">>>>>>>")
        println(">>>>>>>")
        println(">>>>>>>")
        val mailSender = getJavaMailSender()

        val email = SimpleMailMessage()
        email.setSubject("Reset Password")
        email.setText(getResetPasswordEmailBody(resetPasswordToken))
        email.setTo(emailaddress)
        email.setFrom("tymenvanhimme@gmail.com")

        mailSender.send(email)
    }
}