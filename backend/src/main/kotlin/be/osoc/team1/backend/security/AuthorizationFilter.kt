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
 * Check if user passed a valid access token, and if user has right permissions.
 * If a valid access token is found, there is no more need for authentication.
 */
class AuthorizationFilter : OncePerRequestFilter() {
    /**
     * check if request is authorized by token
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // don't check authorization when url is {baseURL}/api/login so everyone can try to log in
        if (request.servletPath.equals("/api/login")) {
            // work here is done, go to next filter
            filterChain.doFilter(request, response)
        } else {
            val authorizationHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
            // authorizationHeader should start with "Basic " followed by token
            if (authorizationHeader?.startsWith("Basic ") == true) {
                // extract access token from authorization header
                val accessToken: String = authorizationHeader.substring("Basic ".length)
                interpretAccessToken(accessToken, request, response, filterChain)
            } else {
                // work here is done, go to next filter
                filterChain.doFilter(request, response)
            }
        }
    }

    /**
     * Interpret access token, extract all useful information and verify its validity
     */
    private fun interpretAccessToken(
        accessToken: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // verify and decode given access token
            val verifier: JWTVerifier = JWT.require(SecretUtil.algorithm).build()
            val decodedJWT: DecodedJWT = verifier.verify(accessToken)
            // extract username from token
            val username: String = decodedJWT.subject

            // Spring security handles auth using an UsernamePasswordAuthenticationToken
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(username, null, getAuthorities(decodedJWT))
            // work here is done, go to next filter
            filterChain.doFilter(request, response)
        } catch (exception: Exception) {
            respondException(response, exception)
        }
    }

    /**
     * extract the roles of the logged-in user from the token
     * return the roles as SimpleGrantedAuthority as they need to be to work with UsernamePasswordAuthenticationToken
     */
    private fun getAuthorities(decodedJWT: DecodedJWT): List<SimpleGrantedAuthority> {
        val roles: Array<String> = decodedJWT.getClaim("roles").asArray(String::class.java)
        val authorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
        roles.forEach { role -> authorities.add(SimpleGrantedAuthority(role)) }
        return authorities
    }

    /**
     * When an error occurs, send a response containing that error
     * This function gets called when a token is passed, but it is invalid
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
