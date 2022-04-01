package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.exceptions.InvalidTokenException
import be.osoc.team1.backend.security.TokenUtil
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

        val decodedToken = TokenUtil.decodeAndVerifyToken(refreshToken)
        if (decodedToken.getClaim("isAccessToken").asBoolean()) {
            throw InvalidTokenException("Expected a refresh token, got an access token.")
        }

        val email: String = decodedToken.subject
        val authorities: List<String> = decodedToken.getClaim("authorities").asList(String::class.java)
        TokenUtil.createAccessAndRefreshToken(response, email, authorities, refreshToken)
    }
}
