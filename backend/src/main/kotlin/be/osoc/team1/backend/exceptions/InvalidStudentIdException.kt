package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by service classes when a given student id does not have a matching
 * record in the database.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class InvalidStudentIdException(
    message: String? = "Wrong student id given",
    cause: Throwable? = null
) : InvalidIdException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}
