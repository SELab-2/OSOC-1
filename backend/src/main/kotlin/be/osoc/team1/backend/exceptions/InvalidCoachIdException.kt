package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by service classes when a given coach id does not have a matching record
 * in the database.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class InvalidCoachIdException(message: String? = "Wrong coach id given", cause: Throwable? = null) :
    InvalidIdException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}
