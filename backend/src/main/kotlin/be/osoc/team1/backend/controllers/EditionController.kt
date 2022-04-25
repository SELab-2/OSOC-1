package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.services.EditionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/editions")
class EditionController(val service: EditionService) {

    /**
     * Creates an inactive edition with the given [edition] name.
     * Returns a 400 (BAD REQUEST) if there is already another edition with the same name.
     */
    @PostMapping
    @Secured("ROLE_ADMIN")
    fun createInactiveEdition(@RequestBody edition: String): ResponseEntity<Edition> {
        // For some reason the edition string is sometimes deserialized as the literal "string" instead of string.
        // This happens when testing in postman, but not in the unit tests.
        val editionWithoutQuotes =
            if (edition.first() == '"' && edition.last() == '"') edition.substring(1, edition.length - 1)
            else edition
        val createdEdition = service.createInactiveEdition(editionWithoutQuotes)
        return getObjectCreatedResponse(createdEdition.name, createdEdition)
    }

    /**
     * Returns the [Edition] object with the given [edition] name,
     * or a 404 (NOT FOUND) if there is no such [Edition].
     */
    @GetMapping("/{edition}")
    @Secured("ROLE_ADMIN")
    fun getEdition(@PathVariable edition: String): Edition = service.getEdition(edition)

    /**
     * Returns the currently active edition, or null if there is no active edition.
     */
    @GetMapping("/active")
    @Secured("ROLE_ADMIN")
    fun getActiveEdition(): Edition? = service.getActiveEdition()

    /**
     * Returns all editions that are currently inactive.
     */
    @GetMapping("/inactive")
    @Secured("ROLE_ADMIN")
    fun getInactiveEditions(): Iterable<Edition> = service.getInactiveEditions()

    /**
     * Activate the given [edition]. Returns a 404 (NOT FOUND) if the edition doesn't exist,
     * a 400 (BAD REQUEST) if the edition is already active,
     * or a 403 (FORBIDDEN) if there is already another active edition.
     */
    @PostMapping("/{edition}/activate")
    @Secured("ROLE_ADMIN")
    fun makeEditionActive(@PathVariable edition: String) = service.makeEditionActive(edition)

    /**
     * Inactivate the given [edition]. The role of all coach users will additionally be changed to disabled.
     * Returns a 404 (NOT FOUND) if the edition doesn't exist, or a 400 (BAD REQUEST) if it is already inactive.
     */
    @PostMapping("/{edition}/inactivate")
    @Secured("ROLE_ADMIN")
    fun makeEditionInactive(@PathVariable edition: String) = service.makeEditionInactive(edition)

    /**
     * Removes the given [edition] from the database. All entities related to the given [edition] are also deleted.
     * Returns a 404 (NOT FOUND) if the [edition] does not exist.
     */
    @DeleteMapping("/{edition}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun deleteEdition(@PathVariable edition: String) = service.deleteEdition(edition)
}
