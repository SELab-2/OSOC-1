package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.services.AssignmentService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/assignments")
class AssignmentController(private val service: AssignmentService) {

    /**
     * Returns the assignment with the corresponding [assignmentId]. If no such assignment exists, returns a
     * "404: Not Found" message instead.
     */
    @GetMapping("/{assignmentId}")
    @Secured("ROLE_COACH")
    fun getAssignmentById(@PathVariable assignmentId: UUID): Assignment = service.getAssignmentById(assignmentId)
}
