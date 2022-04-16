package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.services.StatusSuggestionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/statusSuggestions")
class StatusSuggestionController(service: StatusSuggestionService): BaseController<StatusSuggestion>(service)
