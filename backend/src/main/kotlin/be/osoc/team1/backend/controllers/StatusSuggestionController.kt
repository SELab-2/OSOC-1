package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.services.StatusSuggestionService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/statusSuggestions")
class StatusSuggestionController(
        private val service: StatusSuggestionService) {

    /**
     * Returns the statusSuggestion with the corresponding [statusSuggestionId]. If no such statusSuggestion exists, returns a
     * "404: Not Found" message instead.
     */
    @GetMapping("/{statusSuggestionId}")
    @Secured("ROLE_COACH")
    fun getStatusSuggestionById(@PathVariable statusSuggestionId: UUID): StatusSuggestion =
            service.getStatusSuggestionById(statusSuggestionId)
}