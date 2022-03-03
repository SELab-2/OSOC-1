package be.osoc.team1.backend.exceptions

/**
 * This exception is thrown by service classes when a given id does not have
 * a matching record in the database.
 */
class InvalidIdException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}
