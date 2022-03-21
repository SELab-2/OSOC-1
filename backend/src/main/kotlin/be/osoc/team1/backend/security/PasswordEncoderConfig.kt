package be.osoc.team1.backend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * passwords get encoded before sent over the network
 * Here we use BCryptPasswordEncoder with no arguments, so it uses default argument 10 as strength
 * BCryptPasswordEncoder is currently the most popular password encoder, it is rather slow which makes it more secure.
 */
@Configuration
class PasswordEncoderConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
