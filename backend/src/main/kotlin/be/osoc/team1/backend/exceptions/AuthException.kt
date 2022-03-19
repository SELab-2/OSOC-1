package be.osoc.team1.backend.exceptions

import org.springframework.security.core.AuthenticationException

/**
 * This exception is thrown when something goes wrong with authentication.
 */
class AuthException(msg: String? = null, t: Throwable? = null) : AuthenticationException(msg, t)
