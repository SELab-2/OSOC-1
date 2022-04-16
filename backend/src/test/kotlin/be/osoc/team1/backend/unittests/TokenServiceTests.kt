package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.exceptions.InvalidTokenException
import be.osoc.team1.backend.security.TokenUtil
import be.osoc.team1.backend.security.TokenUtil.decodeAndVerifyToken
import be.osoc.team1.backend.security.TokenUtil.refreshTokenRotation
import be.osoc.team1.backend.services.TokenService
import io.mockk.every
import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class TokenServiceTests {
    private val tokenService = TokenService()

    private val testEmail = "ab@cd.fe"
    private val testAuthorities = listOf("ROLE_COACH")

    @Test
    fun `renewAccessToken fails if no token given`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val exception = assertThrows<InvalidTokenException> { tokenService.renewAccessToken(request, response) }
        assertEquals("No refresh token found in request body.", exception.message)
    }

    @Test
    fun `renewAccessToken fails if access token given`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val accessToken = TokenUtil.createAccessAndRefreshToken(testEmail, testAuthorities).accessToken
        request.addParameter("refreshToken", accessToken)

        val exception = assertThrows<InvalidTokenException> { tokenService.renewAccessToken(request, response) }
        assertEquals("Expected a refresh token, got an access token.", exception.message)
    }

    @Test
    fun `renewAccessToken does not fail`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val refreshToken = TokenUtil.createAccessAndRefreshToken(testEmail, testAuthorities).refreshToken
        request.addParameter("refreshToken", refreshToken)
        val expirationDate = decodeAndVerifyToken(refreshToken).expiresAt

        mockkObject(TokenUtil)
        every { refreshTokenRotation(response, refreshToken, testEmail, testAuthorities, expirationDate) } returns Unit
        assertDoesNotThrow { tokenService.renewAccessToken(request, response) }
    }
}
