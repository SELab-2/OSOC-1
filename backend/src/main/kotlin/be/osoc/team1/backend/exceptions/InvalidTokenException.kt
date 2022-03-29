package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by TokenUtil when a token is signed incorrectly.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class InvalidTokenException(
    message: String = "Invalid token given, integrity of token could not be verified",
    cause: Throwable? = null
) : InvalidIdException(message, cause)
