package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.services.AssignmentService
import be.osoc.team1.backend.services.BaseService
import be.osoc.team1.backend.services.PositionService
import be.osoc.team1.backend.services.StatusSuggestionService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

open class BaseController<T>(open val service: BaseService<T>) {

    /**
     * Returns the T with the corresponding [id]. If no such T exists, returns a
     * "404: Not Found" message instead.
     */
    @GetMapping("/{id}")
    @Secured("ROLE_COACH")
    fun getById(@PathVariable id: UUID): T = service.getById(id)
}

@RestController
@RequestMapping("/assignments")
class AssignmentController(service: AssignmentService) : BaseController<Assignment>(service)

@RestController
@RequestMapping("/positions")
class PositionController(service: PositionService) : BaseController<Position>(service)

@RestController
@RequestMapping("/statusSuggestions")
class StatusSuggestionController(service: StatusSuggestionService) : BaseController<StatusSuggestion>(service)
