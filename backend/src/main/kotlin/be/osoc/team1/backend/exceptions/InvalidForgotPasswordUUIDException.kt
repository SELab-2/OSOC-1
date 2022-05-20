package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
* This exception is thrown when an attempt was made to change a password with an invalid uuid.
*/
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class InvalidForgotPasswordUUIDException(
    message: String = "forgotPasswordUUID is invalid.",
    cause: Throwable? = null
) : Exception(message, cause)
