package be.osoc.team1.backend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * This class is used to encrypt passwords before storing them in the database.
 *
 * In this project we use the [BCryptPasswordEncoder] to encode the passwords. [BCryptPasswordEncoder] is currently the
 * most popular password encoder. It uses the widely supported bcrypt algorithm to hash passwords.
 *
 * The recommendation is that the password encoder should be tuned, so it takes about 1 second to verify a password on
 * your system. This trade-off is to make it difficult for attackers to crack the password using brute-force attacks,
 * but not so costly it puts an excessive burden on your system.
 * More on brute-force attacks can be read on: https://owasp.org/www-community/controls/Blocking_Brute_Force_Attacks
 *
 * [BCryptPasswordEncoder] takes an argument between 4 and 31. This argument decides how 'strong' your password encoder
 * is and thus how long it takes to verify a password. This parameter is logarithmic, and defaults to 10. Each time you
 * increment it you double the amount of work needed, and the time your app will take to check a password.
 *
 * When we need to verify the password without a password encoder enabled, the verification takes about 0.10 seconds.
 * When we use [BCryptPasswordEncoder] while the strength parameter is set to 14, verification will take about 1 second.
 *
 * Below is discussed why [BCryptPasswordEncoder] is chosen over the three other most popular password encoders.
 * Pbkdf2 is not memory hard and thus weaker than bcrypt. Pbkdf2PasswordEncoder is a good choice if FIPS certification
 * would be required.
 * SCryptPasswordEncoder is a good alternative to [BCryptPasswordEncoder], but uses more memory.
 * Argon2 is stronger than bcrypt but only when runtimes exceed 1 second, and thus Argon2PasswordEncoder needs more
 * configuring to be effective on all hardware. Longer encryption runtimes also make for a worse end-user experience.
 *
 * above information is partially based on following link:
 * https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-bcrypt
 */
@Configuration
class PasswordEncoderConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(14)
    }
}
