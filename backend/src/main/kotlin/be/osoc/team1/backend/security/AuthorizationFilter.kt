package be.osoc.team1.backend.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Arrays.stream
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthorizationFilter : OncePerRequestFilter() {
    /**
     * check if request is authorized by token
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // don't check authorization when url is {baseURL}/api/login
        if (request.servletPath.equals("/api/login")) {
            // go to next filter
            filterChain.doFilter(request, response)
        } else {
            val authorizationHeader: String? = request.getHeader(AUTHORIZATION)
            // authorizationHeader should start with "Basic " followed by token
            if (authorizationHeader?.startsWith("Basic ") == true) {
                try {
                    // verify token
                    val token: String = authorizationHeader.substring("Basic ".length)
                    val verifier: JWTVerifier = JWT.require(SecretUtil().algorithm).build()
                    val decodedJWT: DecodedJWT = verifier.verify(token)
                    val username: String = decodedJWT.subject

                    val roles: Array<String> = decodedJWT.getClaim("roles").asArray(String::class.java)
                    val authorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
                    stream(roles).forEach { role -> authorities.add(SimpleGrantedAuthority(role)) }

                    val authenticationToken = UsernamePasswordAuthenticationToken(username, null, authorities)
                    SecurityContextHolder.getContext().authentication = authenticationToken
                    filterChain.doFilter(request, response)
                } catch (e: Exception) {
                    println("MY ERROR: a nonexisting token was given in request")
                    response.setHeader("error", e.message)
                    response.status = HttpStatus.FORBIDDEN.value()
                    response.sendError(HttpStatus.FORBIDDEN.value())
                    val errors: MutableMap<String, String> = HashMap()
                    errors["error_msg"] = e.message as String
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    ObjectMapper().writeValue(response.outputStream, errors)
                }
            } else {
                filterChain.doFilter(request, response)
            }
        }
    }
}
