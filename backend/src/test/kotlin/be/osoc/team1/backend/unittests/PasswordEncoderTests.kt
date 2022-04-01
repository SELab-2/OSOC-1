package be.osoc.team1.backend.unittests

import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class PasswordEncoderTests {
    @Test
    fun `encoding password should take around 1 second`() {
        val startTimeMillis = System.currentTimeMillis()
        BCryptPasswordEncoder(14).encode("Password.test1")
        val passedTimeMillis = System.currentTimeMillis() - startTimeMillis
        assert(passedTimeMillis > 700)
        assert(passedTimeMillis < 1300)
    }
}
