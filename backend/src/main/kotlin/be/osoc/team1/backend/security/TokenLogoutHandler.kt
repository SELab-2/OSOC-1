package be.osoc.team1.backend.security

import be.osoc.team1.backend.security.TokenUtil.decodeAndVerifyToken
import be.osoc.team1.backend.security.TokenUtil.getAccessTokenFromRequest
import be.osoc.team1.backend.security.TokenUtil.invalidateRefreshToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Invalidate refresh token when logging out.
 */
class TokenLogoutHandler : LogoutHandler {
    /**
     * Extract email from access token from [request], then invalidate the refresh token associated with this email.
     * Don't throw an error when logout is called but no user is logged in. It is still useful to invalidate the http
     * session and redirect to '/login?logout'.
     */
    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        try {
            val accessToken = getAccessTokenFromRequest(request!!)

            val decodedToken = decodeAndVerifyToken(accessToken!!)
            val email: String = decodedToken.subject
            invalidateRefreshToken(email)
        } catch (_: Exception) {
            // no user is logged in.
        }
    }
}
