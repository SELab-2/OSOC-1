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
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.servletPath.equals("/api/login")) {
            filterChain.doFilter(request, response)
        } else {
            val authorizationHeader: String? = request.getHeader(AUTHORIZATION)
            if (authorizationHeader?.startsWith("Bearer ") == true) {
                try {
                    val token: String = authorizationHeader.substring("Bearer ".length)
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
                    println("MY ERROR: an error occured in AuthorizationFilter")
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
