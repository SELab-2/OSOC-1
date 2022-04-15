package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.services.PositionService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/positions")
class PositionController(
        private val service: PositionService) {

    /**
     * Returns the position with the corresponding [positionId]. If no such position exists, returns a
     * "404: Not Found" message instead.
     */
    @GetMapping("/{positionId}")
    @Secured("ROLE_COACH")
    fun getPositionById(@PathVariable positionId: UUID): Position = service.getPositionById(positionId)
}