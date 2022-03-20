package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.services.EditionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/edition")
class EditionController(private val service: EditionService) {

    /**
     * Creates a new edition by deleting all students, projects, coaches
     * and disabled users from the database. This has to be done due to GDPR concerns.
     * Be aware that this essentially means the entire database is 'nuked'.
     * Notably, users with the 'Admin' permission level, along with static things like StudentRoles,
     * are not removed from the database.
     */
    @PostMapping("/new")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun createNewEdition() = service.createNewEdition()
}
