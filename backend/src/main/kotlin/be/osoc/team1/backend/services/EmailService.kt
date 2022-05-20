package be.osoc.team1.backend.services

import be.osoc.team1.backend.exceptions.InvalidGmailCredentialsException
import org.springframework.mail.MailAuthenticationException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service
import java.util.Properties
import java.util.UUID

/**
 * This class contains every function needed to make and send emails.
 */
@Service
class EmailService {
    /**
     * Credentials of gmail account to send emails with.
     */
    private var emailAddressSender: String? = System.getenv("OSOC_GMAIL_ADDRESS")
    private var passwordSender: String? = System.getenv("OSOC_GMAIL_APP_PASSWORD")
    lateinit var mailSender: JavaMailSenderImpl

    init {
        initMailSender()
    }

    /**
     * Set credentials of account to send mails with. This function is used for testing.
     */
    fun setSenderEmailCredentials(email: String?, password: String?) {
        emailAddressSender = email
        passwordSender = password
        initMailSender()
    }

    /**
     * Make the body of the email users receive when they request a password change.
     */
    private fun getForgotPasswordEmailBody(forgotPasswordUUID: UUID): String {
        val osocScheme = System.getenv("OSOC_SCHEME") ?: "https"
        val osocUrl = System.getenv("OSOC_URL") ?: "sel2-1.ugent.be"
        val url = "$osocScheme://$osocUrl/forgotPassword/$forgotPasswordUUID"
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
     * Initialise [mailSender].
     */
    private final fun initMailSender() {
        mailSender = JavaMailSenderImpl().apply {
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
     * Set content, title, sender and receiver of email.
     */
    private fun getEmailMessage(emailAddressReceiver: String, forgotPasswordUUID: UUID): SimpleMailMessage {
        return SimpleMailMessage().apply {
            setSubject("Reset Password")
            setText(getForgotPasswordEmailBody(forgotPasswordUUID))
            setTo(emailAddressReceiver)
            setFrom(emailAddressSender!!)
        }
    }

    /**
     * Email [emailAddressReceiver] with a [forgotPasswordUUID], so [emailAddressReceiver] can reset its email.
     */
    fun sendEmail(emailAddressReceiver: String, forgotPasswordUUID: UUID) {
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
