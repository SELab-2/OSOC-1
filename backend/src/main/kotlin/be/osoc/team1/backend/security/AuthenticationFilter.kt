package be.osoc.team1.backend.security

import be.osoc.team1.backend.security.TokenUtil.createAccessAndRefreshToken
import be.osoc.team1.backend.services.OsocUserDetailService
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
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
class AuthenticationFilter(authenticationManager: AuthenticationManager?, private val userDetailsService: OsocUserDetailService) :
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
        val email: String? = request.getParameter("email")?.lowercase()
        val password: String? = request.getParameter("password")
        if (email == null || password == null) {
            throw AuthenticationCredentialsNotFoundException("The \"email\" and \"password\" parameters are required!")
        }
        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
    }

    /**
     * Add an access token, refresh token, TTL of access token and information about the successfully authenticated
     * user to the response.
     */
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        val authenticatedUser: User = authentication.principal as User
        val email: String = authenticatedUser.username
        val authorities: List<String> = authenticatedUser.authorities.map { it.authority }.toList()
        val newTokenResponseData = createAccessAndRefreshToken(email, authorities)

        val osocUser = userDetailsService.getUserFromPrincipal(authentication)
        val authResponseData = AuthResponseData(newTokenResponseData, osocUser)
        authResponseData.addDataToHttpResponse(response)
    }
}
