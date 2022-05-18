package be.osoc.team1.backend.exceptions

import be.osoc.team1.backend.services.TokenService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
/**
 * This exception is thrown by [TokenService] when authorization failed due to an invalid token.
 */
@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
class InvalidRefreshTokenException(
    message: String = "Invalid refresh token given",
    cause: Throwable? = null
) : Exception(message, cause)
