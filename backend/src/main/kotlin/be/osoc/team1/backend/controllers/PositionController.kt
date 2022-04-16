package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.services.PositionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/positions")
class PositionController(service: PositionService) : BaseController<Position>(service)
