package be.osoc.team1.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * This exception is thrown by service classes when a wrong operation was requested
 * (like removing a student from a project when that project doesn't have that student)
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class FailedOperationException(message: String = "Failed operation", cause: Throwable? = null) : Exception(message, cause)
