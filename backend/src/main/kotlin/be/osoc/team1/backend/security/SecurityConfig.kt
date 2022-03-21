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
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig(val userDetailsService: OsocUserDetailService) : WebSecurityConfigurerAdapter() {
    /**
     * handles all incoming requests
     */
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        // don't use cookies
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // everyone has access to following urls (no auth needed)
        http.authorizeRequests().antMatchers(*ConfigUtil.urlsOpenToAll).permitAll()

        // allow registering a user without being authenticated
        http.authorizeRequests().antMatchers(HttpMethod.POST, *ConfigUtil.urlsOpenToAllToPostTo).permitAll()

        // Minimum security permission needed for any other route is the coach role
        http.authorizeRequests().anyRequest().hasAnyAuthority("ROLE_COACH")

        val authenticationFilter = AuthenticationFilter(authenticationManagerBean())
        // check first if authorized using accessToken
        http.addFilterBefore(AuthorizationFilter(), AuthenticationFilter::class.java)
        // if not authorized yet, try to authenticate
        http.addFilter(authenticationFilter)
    }

    @Autowired
    protected fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(userDetailsService.passwordEncoder)
    }
}
