package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.services.TokenService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/token")
class TokenController(private val service: TokenService) {
    /**
     * Get a new access token using the refresh token given in [request].
     */
    @PostMapping("/refresh")
    fun renewAccessToken(request: HttpServletRequest, response: HttpServletResponse) =
        service.renewAccessToken(request, response)

    /**
     * Invalidate refresh token of logged in user.
     */
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse) = service.logout(request, response)
}
