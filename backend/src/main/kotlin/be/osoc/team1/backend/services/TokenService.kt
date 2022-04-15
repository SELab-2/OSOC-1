package be.osoc.team1.backend.services

import be.osoc.team1.backend.exceptions.InvalidTokenException
import be.osoc.team1.backend.security.TokenUtil.decodeAndVerifyToken
import be.osoc.team1.backend.security.TokenUtil.refreshTokenRotation
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class TokenService {
    /**
     * Extract and verify the refresh token from the [request]. Use this refresh token to acquire a new access token.
     */
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
}
