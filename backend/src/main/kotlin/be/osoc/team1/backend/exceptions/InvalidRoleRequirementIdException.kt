import be.osoc.team1.backend.exceptions.InvalidIdException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by service classes when a given role requirement id does not have a matching
 * record in the database.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class InvalidRoleRequirementIdException(
    message: String = "Wrong role requirement id given",
    cause: Throwable? = null
) : InvalidIdException(message, cause)
