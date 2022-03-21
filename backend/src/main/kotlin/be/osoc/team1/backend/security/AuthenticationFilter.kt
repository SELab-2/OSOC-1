package be.osoc.team1.backend.security

import be.osoc.team1.backend.exceptions.AuthException
import com.auth0.jwt.JWT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.Date
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.HashMap

/**
 * When the user is not yet authorized the AuthenticationFilter tries to authenticate the user
 * If the authentication is successful, then this class creates an access token.
 */
class AuthenticationFilter(authenticationManager: AuthenticationManager?) :
    UsernamePasswordAuthenticationFilter(authenticationManager) {

    /**
     * authenticate using email and password from request
     * Throw an [AuthException] if the authentication process fails as described in the spring security docs:
     * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/authentication/UsernamePasswordAuthenticationFilter.html#attemptAuthentication(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)
     * Currently this exception is only thrown if the request didn't contain the email or password parameters.
     */
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val email: String? = request.getParameter("email")
        val password: String? = request.getParameter("password")
        if (email == null || password == null) {
            throw AuthException("The \"email\" and \"password\" parameters are required!")
        }
        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
    }

    /**
     * add an access token to the response
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
        // create tokens
        val accessToken: String = createToken(user, 5)
        // add tokens to response
        val tokens: MutableMap<String, String> = HashMap()
        tokens["accessToken"] = accessToken
        response.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, tokens)
    }

    /**
     * create tokens used for authorization
     * the token contains username, expiration date and roles of user
     */
    private fun createToken(user: User, minutesToLive: Int): String {
        return JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + minutesToLive * 60 * 1000))
            .withClaim("roles", user.authorities.toString())
            .sign(SecretUtil.algorithm)
    }
}
