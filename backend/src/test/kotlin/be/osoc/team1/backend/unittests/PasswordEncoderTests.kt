package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.security.PasswordEncoderConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@UnsecuredWebMvcTest(PasswordEncoderConfig::class)
class PasswordEncoderTests {

    @Autowired
    private lateinit var passwordEncoderConfig: PasswordEncoderConfig

    @Test
    fun `encoding password should take around 1 second`() {
        val encoder = passwordEncoderConfig.passwordEncoder()
        val startTimeMillis = System.currentTimeMillis()
        encoder.encode("Password.test1")
        val passedTimeMillis = System.currentTimeMillis() - startTimeMillis
        assert(passedTimeMillis > 700)
        assert(passedTimeMillis < 2000)
    }

    @Test
    fun `manual encoding test`() {
        val startTimeMillis = System.currentTimeMillis()
        BCryptPasswordEncoder(14).encode("Password.test1")
        val passedTimeMillis = System.currentTimeMillis() - startTimeMillis
        assert(passedTimeMillis > 700)
        assert(passedTimeMillis < 2000)
    }
}
