package be.osoc.team1.backend.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This filter gets called by the filter-chain (see [SecurityConfig] for more info)
 *
 * Authorization will only succeed when the request contains an Authorization header with a valid access token.
 * This access token says which user is logged in and what permissions the user has.
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
        val authorizationHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            val accessToken: String = authorizationHeader.substring("Basic ".length)
            interpretAccessToken(accessToken, request, response, filterChain)
            return
        }

        filterChain.doFilter(request, response)
    }

    /**
     * Interpret access token and verify its validity
     * extract the username and the authorities/roles from the token
     * Catch the error if the given access token is invalid, and add error to response instead
     */
    private fun interpretAccessToken(
        accessToken: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val verifier: JWTVerifier = JWT.require(SecretUtil.algorithm).build()
            val decodedJWT: DecodedJWT = verifier.verify(accessToken)
            val username: String = decodedJWT.subject

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(username, null, getAuthorities(decodedJWT))
            filterChain.doFilter(request, response)
        } catch (exception: Exception) {
            respondException(response, exception)
        }
    }

    /**
     * extract the roles of the logged in user from the token
     * return the roles as [SimpleGrantedAuthority] as they need to be, to work with [UsernamePasswordAuthenticationToken]
     */
    private fun getAuthorities(decodedJWT: DecodedJWT): List<SimpleGrantedAuthority> {
        val roles: Array<String> = decodedJWT.getClaim("roles").asArray(String::class.java)
        val authorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
        roles.forEach { role -> authorities.add(SimpleGrantedAuthority(role)) }
        return authorities
    }

    /**
     * This function gets called when an invalid token is passed, and therefore an error occurs
     * When that error occurs, don't throw it, send a response containing that error instead
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
