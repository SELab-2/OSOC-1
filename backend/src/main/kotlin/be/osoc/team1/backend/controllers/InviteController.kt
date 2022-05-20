package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.services.EmailService
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/invite")
class InviteController(private val emailService: EmailService) {
    /**
     * Send an invitation to [emailAddress] to register.
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_COACH")
    fun inviteEmail(@RequestBody emailAddress: String) = emailService.sendEmail(emailAddress)
}
