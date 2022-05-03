package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.services.ResetPasswordService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/resetpassword")
class ResetPasswordController(private val service: ResetPasswordService) {


}
