package be.osoc.team1.backend.services

import be.osoc.team1.backend.exceptions.InvalidGmailCredentialsException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.mail.MailAuthenticationException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service
import java.util.Properties
import java.util.UUID
import kotlin.collections.set

@Configuration
class MailSenderConfig {
    @Bean
    fun getMailSender(): JavaMailSenderImpl {
        return JavaMailSenderImpl()
    }
}

/**
 * This class contains every function needed to make and send emails.
 */
@Service
class EmailService(environment: Environment, private val mailSender: JavaMailSenderImpl) {
    /**
     * Credentials of gmail account to send emails with.
     */
    private var emailAddressSender: String? = environment["OSOC_GMAIL_ADDRESS"]
    private var passwordSender: String? = environment["OSOC_GMAIL_APP_PASSWORD"]
    private final val baseUrl = environment["OSOC_FRONTEND_URL"] ?: "http://localhost:3000"

    private val invitationMailTitle = "You were invited to join OSOC"
    private val forgotPasswordMailTitle = "Reset Password OSOC"
    private val invitationMailBody: String = """
        Hi there!
        
        Are you looking improve your skills while getting paid?
        Open summer of code allows you to do just that. Work together with other students to bring an idea to life
        in just one month, while being guided by experts in the field. You'll become more independent and
        professional by managing a real project for a real client.
        
        Click the first link below to get started or click the second link below to get more information.
        $baseUrl/register
        https://osoc.be/students
        (if these links aren't clickable, you can copy and paste them into the search bar)
        
        We hope to see you soon!
        Cheers
        The OSOC team
    """.trimIndent()

    /**
     * Initialise the [mailSender].
     */
    init {
        mailSender.apply {
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
    }

    /**
     * Make the body of the email which users receive when they request a password change.
     */
    private fun getForgotPasswordMailBody(forgotPasswordUUID: UUID): String {
        val url = "$baseUrl/forgotPassword/$forgotPasswordUUID"
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
     * Set content, title, sender and receiver of email.
     */
    private fun getEmailMessage(emailAddressReceiver: String, forgotPasswordUUID: UUID?): SimpleMailMessage {
        return SimpleMailMessage().apply {
            if (forgotPasswordUUID == null) {
                setSubject(invitationMailTitle)
                setText(invitationMailBody)
            } else {
                setSubject(forgotPasswordMailTitle)
                setText(getForgotPasswordMailBody(forgotPasswordUUID))
            }
            setTo(emailAddressReceiver)
            setFrom(emailAddressSender!!)
        }
    }

    /**
     * If [forgotPasswordUUID] is null, an invitation mail gets send to [emailAddressReceiver].
     * Otherwise, email [emailAddressReceiver] a link to reset its password.
     */
    fun sendEmail(emailAddressReceiver: String, forgotPasswordUUID: UUID? = null) {
        if (emailAddressSender == null || passwordSender == null) {
            throw InvalidGmailCredentialsException("No 'OSOC_GMAIL_ADDRESS' or 'OSOC_GMAIL_APP_PASSWORD' found in environment variables.")
        }
        val emailMessage = getEmailMessage(emailAddressReceiver, forgotPasswordUUID)
        try {
            mailSender.send(emailMessage)
        } catch (_: MailAuthenticationException) {
            throw InvalidGmailCredentialsException(
                "Make sure 'OSOC_GMAIL_ADDRESS' and 'OSOC_GMAIL_APP_PASSWORD' are correctly configured in environment" +
                    "variables. 'OSOC_GMAIL_APP_PASSWORD' should be set to a gmail app password, a normal password won't work here."
            )
        }
    }
}
