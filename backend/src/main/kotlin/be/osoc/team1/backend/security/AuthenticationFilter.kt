package be.osoc.team1.backend.security

import be.osoc.team1.backend.entities.EntityViews
import be.osoc.team1.backend.services.OsocUserDetailService
import com.auth0.jwt.JWT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.Date
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This filter gets called by the filter-chain (see [SecurityConfig] for more info)
 *
 * Authentication will only succeed when the request contains valid user credentials (email and password).
 * If the authentication is successful, then a response gets send with a new access token.
 * The now authenticated user can use this access token to authorize himself in the following requests.
 */
class AuthenticationFilter(authenticationManager: AuthenticationManager?, val userDetailsService: OsocUserDetailService) :
    UsernamePasswordAuthenticationFilter(authenticationManager) {

    /**
     * Authenticate using email and password from request.
     *
     * Throws an [AuthenticationException] if the authentication process fails as described in the spring security docs:
     * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/authentication/UsernamePasswordAuthenticationFilter.html#attemptAuthentication(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)
     *
     * An [AuthenticationCredentialsNotFoundException] exception is thrown if the request didn't contain the email or
     * password parameters. Other [AuthenticationException]s can be thrown if there is a failure in
     * [AuthenticationManager.authenticate].
     */
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val email: String? = request.getParameter("email")
        val password: String? = request.getParameter("password")
        if (email == null || password == null) {
            throw AuthenticationCredentialsNotFoundException("The \"email\" and \"password\" parameters are required!")
        }
        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
    }

    /**
     * add an access token to the response when authentication is successful
     * this token can be used by the user to authorise itself in the following requests
     */
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        val authenticatedUser: User = authentication.principal as User
        val accessToken: String = createToken(authenticatedUser, 5)

        val osocUser = userDetailsService.getUserFromPrincipal(authentication)
        val authResponse = AuthResponse(accessToken, osocUser)
        response.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writerWithView(EntityViews.Public::class.java).writeValue(response.outputStream, authResponse)
    }

    /**
     * create a JSON web token
     * the token contains username, expiration date and roles of user
     * this function can be used for making an access token or even a refresh token
     */
    private fun createToken(user: User, minutesToLive: Int): String {
        val roles: List<String> =
            user.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
        return JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + minutesToLive * 60 * 1000))
            .withClaim("roles", roles)
            .sign(SecretUtil.algorithm)
    }

    data class AuthResponse(val accessToken: String, val user: be.osoc.team1.backend.entities.User)
}
