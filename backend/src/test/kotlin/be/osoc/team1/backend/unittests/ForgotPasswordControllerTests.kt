package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.ForgotPasswordController
import be.osoc.team1.backend.exceptions.InvalidForgotPasswordUUIDException
import be.osoc.team1.backend.security.PasswordEncoderConfig
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.ForgotPasswordService
import be.osoc.team1.backend.services.OsocUserDetailService
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
import java.util.UUID

@UnsecuredWebMvcTest(ForgotPasswordController::class, PasswordEncoderConfig::class)
class ForgotPasswordControllerTests(@Autowired val mockMvc: MockMvc) {
    @MockkBean
    private lateinit var forgotPasswordService: ForgotPasswordService
    @MockkBean
    private lateinit var editionService: EditionService
    @MockkBean
    private lateinit var osocUserDetailService: OsocUserDetailService

    private val validUUID = UUID.randomUUID()
    private val validNewPassword = "valid_new_password"

    @Test
    fun `postEmail should not fail`() {
        every { forgotPasswordService.sendEmailWithToken(any()) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.post("/forgotPassword")
                .contentType(MediaType.TEXT_PLAIN)
                .content(ObjectMapper().writeValueAsString("invalid.mail"))
        ).andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun `changePassword should not fail when valid uuid given`() {
        every { forgotPasswordService.changePassword(validUUID, validNewPassword) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/forgotPassword/$validUUID")
                .contentType(MediaType.TEXT_PLAIN)
                .content(validNewPassword)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `changePassword should fail when invalid uuid given`() {
        val invalidUUID = UUID.randomUUID()
        every { forgotPasswordService.changePassword(invalidUUID, validNewPassword) } throws InvalidForgotPasswordUUIDException()
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/forgotPassword/$invalidUUID")
                .contentType(MediaType.TEXT_PLAIN)
                .content(validNewPassword)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}
