package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by TODO
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class BadFilterException(message: String = "Bad filters given", cause: Throwable? = null) : Exception(message, cause)
