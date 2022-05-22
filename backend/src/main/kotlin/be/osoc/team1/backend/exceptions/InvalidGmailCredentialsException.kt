package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by [EmailUtil] when invalid gmail credentials given for mail server.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class InvalidGmailCredentialsException(
    message: String = "Invalid gmail credentials given for mail server",
    cause: Throwable? = null
) : Exception(message, cause)
