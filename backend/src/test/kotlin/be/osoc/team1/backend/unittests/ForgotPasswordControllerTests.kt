package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.ForgotPasswordController
import be.osoc.team1.backend.controllers.UserController
import be.osoc.team1.backend.security.PasswordEncoderConfig
import be.osoc.team1.backend.services.ForgotPasswordService
import be.osoc.team1.backend.services.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@UnsecuredWebMvcTest(ForgotPasswordController::class, PasswordEncoderConfig::class)
class ForgotPasswordControllerTests(@Autowired val mockMvc: MockMvc) {
    @MockkBean
    private lateinit var service: ForgotPasswordService

    @Test
    fun `postEmail should not fail`() {
        every { service.sendEmailWithToken(any()) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.post("/forgotPassword")
                .contentType(MediaType.TEXT_PLAIN)
                .content(ObjectMapper().writeValueAsString("test@mail.com"))
        ).andExpect(MockMvcResultMatchers.status().isNoContent)
    }
}