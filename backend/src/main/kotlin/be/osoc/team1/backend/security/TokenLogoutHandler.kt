package be.osoc.team1.backend.security

import be.osoc.team1.backend.exceptions.InvalidTokenException
import be.osoc.team1.backend.security.TokenUtil.getAccessTokenFromRequest
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenLogoutHandler : LogoutHandler {
    /**
     * Extract email from access token from [request], then invalidate the refresh token associated with this email.
     */
    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        try {
            val accessToken = getAccessTokenFromRequest(request!!)

            val decodedToken = TokenUtil.decodeAndVerifyToken(accessToken!!)
            val email: String = decodedToken.subject
            TokenUtil.invalidateRefreshToken(email)
        } catch (e:Exception) {
            throw InvalidTokenException("You need to be logged in to be able to log out.")
        }
    }
}
