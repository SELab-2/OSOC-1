package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.services.ForgotPasswordService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/forgotPassword")
class ForgotPasswordController(private val service: ForgotPasswordService) {
    /**
     * Request to reset the password of the user with given [emailAddress]. The link to actually reset the password is
     * sent in an email to [emailAddress].
     * This request will always succeed, even when an invalid [emailAddress] is given. Otherwise, people with bad intent
     * could track down which email addresses are linked to existing accounts.
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun postEmail(@RequestBody emailAddress: String) = service.sendEmailWithToken(emailAddress)

    /**
     * Reset password using [resetPasswordUUID].
     */
    @PatchMapping("/{resetPasswordUUID}")
    fun patchPassword(@PathVariable resetPasswordUUID: UUID, @RequestBody newPassword: String) =
        service.changePassword(resetPasswordUUID, newPassword)
}
