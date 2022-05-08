package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.security.PasswordEncoderConfig
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.OsocUserDetailService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@UnsecuredWebMvcTest(PasswordEncoderConfig::class)
class PasswordEncoderTests {

    @Autowired
    private lateinit var passwordEncoderConfig: PasswordEncoderConfig

    @MockkBean
    private lateinit var editionService: EditionService

    @MockkBean
    private lateinit var osocUserDetailService: OsocUserDetailService

    @BeforeEach
    fun beforeEach() {
        every { osocUserDetailService.getUserFromPrincipal(any()) } returns User("", "", Role.Admin, "")
        every { editionService.getEdition(any()) } returns Edition("", true)
    }

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
