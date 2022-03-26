import be.osoc.team1.backend.exceptions.InvalidIdException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by service classes when a given assignment id does not have a matching
 * record in the database.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class InvalidAssignmentIdException(
    message: String = "Wrong assignment id given",
    cause: Throwable? = null
) : InvalidIdException(message, cause)
