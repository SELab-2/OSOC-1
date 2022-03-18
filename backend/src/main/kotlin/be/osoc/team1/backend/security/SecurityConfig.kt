package be.osoc.team1.backend.security

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
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    /**
     * UserDetailsService now just returns User Object, should be replaced by UserRepo
     * This is our very simple User database at the moment
     */
    private val userDetailsService: UserDetailsService = UserDetailsService { User("user", passwordEncoder.encode("pass"), mutableListOf(SimpleGrantedAuthority("ROLE_USER"))) }

    /**
     * used to encode password, so it can't be read by outsiders
     */
    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()

    /**
     * handles all incoming requests
     */
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // permit following urls
        http.authorizeRequests().antMatchers("/", "/login", "/logout").permitAll()

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
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }

    /**
     * get AuthenticationManager
     */
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}
