package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Answer
import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.exceptions.UnauthorizedOperationException
import be.osoc.team1.backend.services.AnswerService
import be.osoc.team1.backend.services.AssignmentService
import be.osoc.team1.backend.services.BaseService
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.OsocUserDetailService
import be.osoc.team1.backend.services.PositionService
import be.osoc.team1.backend.services.SkillService
import be.osoc.team1.backend.services.StatusSuggestionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

abstract class BaseController<T : Any, K>(open val service: BaseService<T, K>) {
    @Autowired
    lateinit var editionService: EditionService

    @Autowired
    lateinit var userDetailService: OsocUserDetailService

    @Autowired
    private lateinit var request: HttpServletRequest

    /**
     * Checks if this [entity] can be accessed by the requesting user.
     * If the edition of the [entity] isn't active and the requesting user isn't admin [attemptEditionAccess] will throw an [UnauthorizedOperationException]
     */
    private fun attemptAccess(entity: T): T {
        val editionName = entity::class.java.getDeclaredField("edition").get(entity) as String
        attemptEditionAccess(editionName, editionService, userDetailService, request)

        return entity
    }

    /**
     * Returns the [T] with the corresponding [id]. If no such [T] exists, returns a
     * "404: Not Found" message instead.
     */
    @GetMapping("/{id}")
    @Secured("ROLE_COACH")
    open fun getById(@PathVariable id: K): T = attemptAccess(service.getById(id))
}

abstract class BaseAllController<T : Any, K>(service: BaseService<T, K>) : BaseController<T, K>(service) {

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
@RequestMapping("/answers")
class AnswerController(service: AnswerService) : BaseController<Answer, UUID>(service)

@RestController
@RequestMapping("/skills")
class SkillController(service: SkillService) : BaseAllController<Skill, String>(service)
