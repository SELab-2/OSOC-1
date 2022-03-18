package be.osoc.team1.backend.security

import com.auth0.jwt.JWT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.Date
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.HashMap

class AuthenticationFilter(authenticationManager: AuthenticationManager?) :
    UsernamePasswordAuthenticationFilter(authenticationManager) {

    /**
     * authenticate using username and password from request
     */
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val username: String = request!!.getParameter("username")
        val password: String = request.getParameter("password")
        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
    }

    /**
     * add an accessToken and refreshToken to response
     * this token should be used for authorization in following requests
     */
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        // authenticated user
        val user: User = authentication.principal as User
        // init tokens
        val accessToken: String = JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 5 * 60 * 1000))
            .withClaim("roles", user.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .sign(SecretUtil().algorithm)
        val refreshToken: String = JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000))
            .withClaim("roles", user.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .sign(SecretUtil().algorithm)
        // add tokens to response
        val tokens: MutableMap<String, String> = HashMap()
        tokens["accessToken"] = accessToken
        tokens["refreshToken"] = refreshToken
        response.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, tokens)
    }
}
