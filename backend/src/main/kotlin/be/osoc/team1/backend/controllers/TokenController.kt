package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.exceptions.InvalidTokenException
import be.osoc.team1.backend.security.TokenUtil.decodeAndVerifyToken
import be.osoc.team1.backend.security.TokenUtil.invalidateRefreshToken
import be.osoc.team1.backend.security.TokenUtil.refreshTokenRotation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/token")
class TokenController {
    /**
     * Get a new access token using your refresh token.
     */
    @PostMapping("/refresh")
    fun renewAccessToken(request: HttpServletRequest, response: HttpServletResponse) {
        val refreshToken: String = request.getParameter("refreshToken")
            ?: throw InvalidTokenException("No refresh token found in request body.")

        val decodedToken = decodeAndVerifyToken(refreshToken)
        if (decodedToken.getClaim("isAccessToken").asBoolean()) {
            throw InvalidTokenException("Expected a refresh token, got an access token.")
        }

        val email: String = decodedToken.subject
        val authorities: List<String> = decodedToken.getClaim("authorities").asList(String::class.java)
        refreshTokenRotation(response, refreshToken, email, authorities, decodedToken.expiresAt)
    }

    /**
     * Extract email from token from [request], then invalidate the refresh token associated with this email.
     */
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        val token: String = request.getParameter("token")
            ?: throw InvalidTokenException("No token found in request body.")

        val decodedToken = decodeAndVerifyToken(token)
        val email: String = decodedToken.subject
        invalidateRefreshToken(email)
    }
}
