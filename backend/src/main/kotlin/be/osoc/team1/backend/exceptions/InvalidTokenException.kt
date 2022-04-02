package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by TokenUtil when authorization failed due to an invalid token.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class InvalidTokenException(
    message: String = "Invalid token given",
    cause: Throwable? = null
) : InvalidIdException(message, cause)
