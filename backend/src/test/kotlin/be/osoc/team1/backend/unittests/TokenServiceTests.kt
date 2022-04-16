package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.exceptions.InvalidTokenException
import be.osoc.team1.backend.services.TokenService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class TokenServiceTests {
    private val tokenService = TokenService()

    @Test
    fun `renewAccessToken fails if no refresh token given`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val exception = assertThrows<InvalidTokenException> { tokenService.renewAccessToken(request, response) }
        assertEquals("No refresh token found in request body.", exception.message)
    }
}
