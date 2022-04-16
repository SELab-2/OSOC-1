package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.services.AssignmentService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/assignments")
class AssignmentController(service: AssignmentService): BaseController<Assignment>(service)
