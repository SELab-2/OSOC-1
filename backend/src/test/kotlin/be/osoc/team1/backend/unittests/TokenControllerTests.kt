package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.TokenController
import be.osoc.team1.backend.services.TokenService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@UnsecuredWebMvcTest(TokenController::class)
class TokenControllerTests(@Autowired private val mockMvc: MockMvc) {
    @MockkBean
    private lateinit var tokenService: TokenService

    @Test
    fun `renewAccessToken should not fail`() {
        every { tokenService.renewAccessToken(any(), any()) } returns Unit
        mockMvc.perform(post("/token/refresh")).andExpect(status().isOk)
    }
}
