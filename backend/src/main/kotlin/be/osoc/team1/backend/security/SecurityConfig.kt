package be.osoc.team1.backend.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    /* 
        This is temporary placeholder code for frontend to work during production.
        This should NOT get used in a production environment.
    */
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests().anyRequest().permitAll()
        http.csrf().disable()
        http.cors().disable()
    }
}
