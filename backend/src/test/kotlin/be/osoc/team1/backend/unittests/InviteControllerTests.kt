package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.InviteController
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.EmailService
import be.osoc.team1.backend.services.OsocUserDetailService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@UnsecuredWebMvcTest(InviteController::class)
class InviteControllerTests(@Autowired val mockMvc: MockMvc) {
    private val testEmail = "test@mail.com"
    @MockkBean
    private lateinit var emailService: EmailService
    @MockkBean
    private lateinit var editionService: EditionService
    @MockkBean
    private lateinit var osocUserDetailService: OsocUserDetailService

    @Test
    fun `inviteEmail should not fail`() {
        every { emailService.sendEmail(testEmail, any()) } just Runs
        mockMvc
            .perform(MockMvcRequestBuilders.post("/invite").content(testEmail))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
