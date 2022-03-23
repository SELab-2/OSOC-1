package be.osoc.team1.backend.security

import be.osoc.team1.backend.services.OsocUserDetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

/**
 * [SecurityConfig] sets the configuration of how requests are handled, how users are authenticated and authorized.
 *
 * Authentication is verifying the identity of a user (using an email and a password). Authorization is verifying which
 * resources this user has access to.
 *
 * Every incoming request will be handled by our [SecurityConfig] class. Some urls will be set to be accessible to all,
 * other urls will require authorization to be accessed. Those requests that need authorization will get processed by
 * the filter-chain. The filter-chain is just a list of filters that get called in a pre-configured order.
 * The first filter in the filter-chain is the [AuthorizationFilter] and tries to authorize the request. If that fails,
 * then the filter-chain proceeds to the next filter, the [AuthenticationFilter] which tries to authenticate the request.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig(val userDetailsService: OsocUserDetailService) : WebSecurityConfigurerAdapter() {
    /**
     * set configuration to handle all incoming requests
     * authentication and authorization are configured to work stateless and thus to use tokens instead of cookies
     * Because we do not use cookies, there is no room for CSRF attacks, and no reason to put in CSRF protection
     * First add [AuthorizationFilter] to check if user is authorized, if not, try to authenticate with the [AuthenticationFilter]
     */
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()

        http.sessionManagement().maximumSessions(1)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.authorizeRequests().antMatchers(*ConfigUtil.urlsOpenToAll).permitAll()
        http.authorizeRequests().antMatchers(HttpMethod.POST, *ConfigUtil.urlsOpenToAllToPostTo).permitAll()
        http.authorizeRequests().anyRequest().hasAnyAuthority("ROLE_COACH")

        val authenticationFilter = AuthenticationFilter(authenticationManagerBean())
        http.addFilterBefore(AuthorizationFilter(), AuthenticationFilter::class.java)
        http.addFilter(authenticationFilter)
    }

    /**
     * configure the right [userDetailsService] to work with our user database
     */
    @Autowired
    protected fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(userDetailsService.passwordEncoder)
    }
}
