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
 * configuration of which urls require which authorizations
 * configuration of how authentication and authorization are handled
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
