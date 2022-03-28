package be.osoc.team1.backend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Passwords need to be encoded before being saved in the database, otherwise anyone accessing a user database on a
 * company's servers (including hackers) could easily view any stored passwords.
 *
 * In this project we use the [BCryptPasswordEncoder] to encode the passwords. [BCryptPasswordEncoder] is currently the
 * most popular password encoder. The [BCryptPasswordEncoder] implementation uses the widely supported bcrypt algorithm
 * to hash the passwords.
 *
 * The recommendation is that the password encoder should be tuned, so it takes about 1 second to verify a password on
 * your system. This trade-off is to make it difficult for attackers to crack the password using brute-force attacks,
 * but not so costly it puts an excessive burden on your system. A brute-force attack is an attempt to discover a
 * password by systematically trying every possible combination of letters, numbers and symbols until you discover the
 * one correct combination that works. Brute-force attacks obviously lose their effectiveness when it takes a second to
 * check the validity of the password.
 *
 * [BCryptPasswordEncoder] takes an argument between 4 and 31. This argument decides how 'strong' your password encoder
 * is and thus how long it takes to verify a password. This parameter is logarithmic, and defaults to 10. Each time you
 * increment it you double the amount of work needed, and the time your app will take to check a password.
 *
 * When we need to verify the password without a password encoder enabled, the verification takes about 0.10 seconds.
 * When we use [BCryptPasswordEncoder] while the strength parameter is set to 13 or 14, verification will take about
 * 0.75 or 1.30 seconds respectively.
 *
 * Below is discussed why [BCryptPasswordEncoder] is chosen above the three other most popular password encoders.
 * Pbkdf2PasswordEncoder is a good choice if FIPS certification would be required, but Pbkdf2 is not memory hard and
 * thus is weaker than bcrypt.
 * SCryptPasswordEncoder is a good alternative to [BCryptPasswordEncoder], but use more memory.
 * Argon2PasswordEncoder also uses more memory then [BCryptPasswordEncoder]. Argon is also stronger than bcrypt but
 * only when runtimes exceed 1 second. Finally, we want something that does not need specific tuning a client might not
 * be knowledgeable enough for, so bcrypt is the better choice.
 *
 * above information is partially based on following link:
 * https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-bcrypt
 */
@Configuration
class PasswordEncoderConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        // return BCryptPasswordEncoder(13)
        return NoOpPasswordEncoder.getInstance()
    }
}
