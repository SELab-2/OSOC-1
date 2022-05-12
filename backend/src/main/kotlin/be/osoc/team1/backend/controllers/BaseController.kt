package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Answer
import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.repositories.AnswerRepository
import be.osoc.team1.backend.services.AnswerService
import be.osoc.team1.backend.services.AssignmentService
import be.osoc.team1.backend.services.BaseService
import be.osoc.team1.backend.services.PositionService
import be.osoc.team1.backend.services.SkillService
import be.osoc.team1.backend.services.StatusSuggestionService
import be.osoc.team1.backend.services.StudentService
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

abstract class BaseController<T, K>(open val service: BaseService<T, K>) {

    /**
     * Returns the [T] with the corresponding [id]. If no such [T] exists, returns a
     * "404: Not Found" message instead.
     */
    @GetMapping("/{id}")
    @Secured("ROLE_COACH")
    fun getById(@PathVariable id: K): T = service.getById(id)
}

abstract class BaseAllController<T, K>(service: BaseService<T, K>) : BaseController<T, K>(service) {

    /**
     * Returns all objects of type [T].
     */
    @GetMapping
    @Secured("ROLE_COACH")
    fun getAll(): Iterable<T> = service.getAll()
}

@RestController
@RequestMapping("/assignments")
class AssignmentController(service: AssignmentService) : BaseController<Assignment, UUID>(service)

@RestController
@RequestMapping("/positions")
class PositionController(service: PositionService) : BaseController<Position, UUID>(service)

@RestController
@RequestMapping("/statusSuggestions")
class StatusSuggestionController(service: StatusSuggestionService) : BaseController<StatusSuggestion, UUID>(service)

@RestController
@RequestMapping("/skills")
class SkillController(service: SkillService) : BaseAllController<Skill, String>(service)
