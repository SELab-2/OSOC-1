package be.osoc.team1.backend.security

import be.osoc.team1.backend.services.OsocUserDetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class SecurityConfig(val userDetailsService: OsocUserDetailService) : WebSecurityConfigurerAdapter() {
    /**
     * handles all incoming requests
     */
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // permit following urls
        http.authorizeRequests().antMatchers("/", "/login", "/logout").permitAll()

        // allow registering a user without being authenticated
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/users").permitAll()

        // check if user has the right permissions
        http.authorizeRequests().antMatchers(HttpMethod.GET).hasAnyAuthority("ROLE_USER")
        http.authorizeRequests().antMatchers(HttpMethod.POST).hasAnyAuthority("ROLE_ADMIN")
        http.authorizeRequests().anyRequest().authenticated()

        // ask to authenticate if not already authorized
        val authenticationFilter = AuthenticationFilter(authenticationManagerBean())
        http.addFilter(authenticationFilter)
        http.addFilterBefore(AuthorizationFilter(), authenticationFilter::class.java)
    }

    @Autowired
    @Throws(Exception::class)
    protected fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(userDetailsService.passwordEncoder)
    }

    /**
     * get AuthenticationManager
     */
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}
