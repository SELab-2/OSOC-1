package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by service classes when a given id does not have a matching record in
 * the database.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
open class InvalidIdException(message: String = "Invalid id", cause: Throwable? = null) :
    Exception(message, cause)
