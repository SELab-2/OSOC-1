package be.osoc.team1.backend.security

import be.osoc.team1.backend.security.TokenUtil.decodeAndVerifyToken
import be.osoc.team1.backend.security.TokenUtil.getAccessTokenFromRequest
import be.osoc.team1.backend.security.TokenUtil.invalidateRefreshToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Invalidate refresh token when logging out.
 */
class TokenLogoutHandler : LogoutSuccessHandler {
    /**
     * Extract email from access token from [request], then invalidate the refresh token associated with this email.
     */
    override fun onLogoutSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        try {
            val accessToken = getAccessTokenFromRequest(request!!)
            val decodedToken = decodeAndVerifyToken(accessToken!!)
            val email: String = decodedToken.subject
            invalidateRefreshToken(email)
            response?.status = HttpServletResponse.SC_OK
        } catch (_: Exception) {
            response?.status = HttpServletResponse.SC_UNAUTHORIZED
        }
    }
}
