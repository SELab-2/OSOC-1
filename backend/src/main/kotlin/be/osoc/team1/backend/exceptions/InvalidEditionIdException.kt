package be.osoc.team1.backend.exceptions

class InvalidEditionIdException(
    message: String = "This edition does not exist.",
    cause: Throwable? = null
) : InvalidIdException(message, cause)
