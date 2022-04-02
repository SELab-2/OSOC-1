package be.osoc.team1.backend.security

import be.osoc.team1.backend.security.TokenUtil.authenticateWithAccessToken
import be.osoc.team1.backend.security.TokenUtil.decodeAndVerifyToken
import be.osoc.team1.backend.security.TokenUtil.getAccessTokenFromRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This filter gets called by the filter-chain (see [SecurityConfig] for more info)
 *
 * Authorization will only succeed when the request contains an Authorization header with a valid access token.
 * This access token says which user is logged in and what permissions he has.
 *
 * The difference between [AuthenticationFilter] and this class([AuthorizationFilter]) is that [AuthorizationFilter]
 * manages the authorities of users, or in other words what they are allowed to do. The [AuthenticationFilter] on the
 * other hand is meant to verify the identity of a user by checking their credentials.
 */
class AuthorizationFilter : OncePerRequestFilter() {
    /**
     * extract access token from authorization header in request, and process the access token
     * when this function is finished, just pass the request and response to the next filter ([AuthenticationFilter])
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val accessToken: String = getAccessTokenFromRequest(request)
            val decodedToken = decodeAndVerifyToken(accessToken)
            authenticateWithAccessToken(decodedToken)
            filterChain.doFilter(request, response)
        } catch (exception: Exception) {
            respondException(response, exception)
        }
    }

    /**
     * This function gets called when an invalid token is passed, and therefore an error occurs.
     * When that error occurs, don't throw it, send a response containing that error instead.
     */
    private fun respondException(response: HttpServletResponse, exception: Exception) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.sendError(HttpStatus.UNAUTHORIZED.value())
        val errors: MutableMap<String, String> = HashMap()
        errors["error_msg"] = exception.message as String
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, errors)
    }
}
