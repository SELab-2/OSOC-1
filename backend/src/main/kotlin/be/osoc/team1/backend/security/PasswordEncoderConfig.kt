package be.osoc.team1.backend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Passwords get encoded before being sent over the network.
 *
 * Here we use BCryptPasswordEncoder with no arguments, so it uses default argument 10 as strength. The
 * BCryptPasswordEncoder should take roughly 1 second to verify a password when strength 10 is used.
 * BCryptPasswordEncoder is currently the most used password encoder. The BCryptPasswordEncoder implementation uses the
 * widely supported bcrypt algorithm to hash the passwords
 *
 * In order to make it more resistant to password cracking, bcrypt is deliberately slow, because if it takes more time
 * to hash the value, it also takes a much longer time to brute-force the password.
 *
 * Pbkdf2PasswordEncoder is a good choice if FIPS certification would be required, but Pbkdf2 is not memory hard and
 * thus is weaker than bcrypt
 * Argon2PasswordEncoder and SCryptPasswordEncoder are good alternatives but use more memory. Argon is stronger than
 * bcrypt but only when runtimes exceed 1000ms, we want something that does not need specific tuning a client might not
 * be knowledgeable enough for, so bcrypt is the better choice.
 *
 * above information is based on following link:
 * https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-bcrypt
 */
@Configuration
class PasswordEncoderConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
