package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by service classes when the request was correct and understood but the action is forbidden.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
class ForbiddenOperationException(message: String? = "Forbidden operation", cause: Throwable? = null) : Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}
