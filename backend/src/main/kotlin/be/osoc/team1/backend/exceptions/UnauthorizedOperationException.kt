package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by service classes when the request is correct but the user doesn't have the authorization
 * to perform the operation.
 * (like removing a suggestion made by someone else)
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class UnauthorizedOperationException(message: String = "Unauthorized operation", cause: Throwable? = null) : Exception(message, cause)
