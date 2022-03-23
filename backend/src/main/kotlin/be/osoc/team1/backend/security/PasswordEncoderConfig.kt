package be.osoc.team1.backend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Passwords get encoded before being sent over the network.
 *
 * Here we use BCryptPasswordEncoder with no arguments, so it uses default argument 10 as strength. The
 * BCryptPasswordEncoder takes roughly 1 second to verify a password when strength 10 is used.
 * BCryptPasswordEncoder is currently the most used password encoder. The BCryptPasswordEncoder implementation uses the
 * widely supported bcrypt algorithm to hash the passwords
 *
 * In order to make it more resistant to password cracking, bcrypt is deliberately slow, because if it takes more time
 * to hash the value, it also takes a much longer time to brute-force the password.
 *
 * Pbkdf2PasswordEncoder would be a better choice if FIPS certification would be required
 * Argon2PasswordEncoder and SCryptPasswordEncoder are good alternatives but use more memory
 */
@Configuration
class PasswordEncoderConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
