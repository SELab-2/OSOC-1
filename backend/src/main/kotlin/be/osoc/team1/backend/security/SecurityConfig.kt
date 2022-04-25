package be.osoc.team1.backend.security

import be.osoc.team1.backend.services.OsocUserDetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * [SecurityConfig] sets the configuration of how requests are handled, how users are authenticated and authorized.
 *
 * Authentication is verifying the identity of a user (using an email and a password). Authorization is verifying which
 * resources this user has access to.
 *
 * Every incoming request will be handled by our [SecurityConfig] class. Urls defined in [ConfigUtil] will be open to
 * non-authenticated users. All other urls require authorization. Those requests that need authorization will get
 * processed by the filter-chain. The filter-chain is a list of filters that get called in a pre-configured order.
 * The first filter in the filter-chain is the [AuthorizationFilter] and tries to authorize the request. If that fails,
 * then the filter-chain proceeds to the next filter, the [AuthenticationFilter] which tries to authenticate the request.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig(val userDetailsService: OsocUserDetailService) : WebSecurityConfigurerAdapter() {
    /**
     * Set configuration to handle all incoming requests.
     *
     * In this function, we configure Spring Security to only work stateless and thus not use any cookies. With this
     * Spring Security configuration, we ensure the browser is not responsible for automatic authentication. This means
     * we can safely disable CSRF protection, since CSRF attacks rely on cookie-based authentication.
     * More on CSRF attacks can be read on: https://owasp.org/www-community/attacks/csrf
     *
     * First add [AuthorizationFilter] to check if user is authorized, if not, try to authenticate with the
     * [AuthenticationFilter].
     */
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()

        http.cors().configurationSource(corsConfigurationSource())

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.authorizeRequests().antMatchers(*ConfigUtil.urlsOpenToAll).permitAll()
        http.authorizeRequests().antMatchers(HttpMethod.POST, *ConfigUtil.urlsOpenToAllToPostTo).permitAll()
        http.authorizeRequests().anyRequest().hasAnyAuthority("ROLE_COACH")

        http.logout { it.addLogoutHandler(TokenLogoutHandler()) }

        val authenticationFilter = AuthenticationFilter(authenticationManagerBean(), userDetailsService)
        http.addFilterBefore(AuthorizationFilter(), AuthenticationFilter::class.java)
        http.addFilter(authenticationFilter)
    }

    /**
     * Set up cors configuration to allow any request made from origins included in [ConfigUtil.allowedCorsOrigins]
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = ConfigUtil.allowedCorsOrigins
        configuration.allowedMethods = listOf("*") // Allow all methods
        configuration.allowedHeaders = listOf("*") // Allow all headers
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // Set this cors configuration for all endpoints
        return source
    }

    /**
     * configure [AuthenticationManagerBuilder] to use our [OsocUserDetailService]
     * This configuration allows to easily log in users from our database using email and password
     */
    @Autowired
    protected fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(userDetailsService.passwordEncoder)
    }
}
