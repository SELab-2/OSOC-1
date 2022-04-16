package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.services.EditionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/editions")
class EditionController(val service: EditionService) {

    /**
     * Returns the currently active edition, or null if there is no active edition.
     */
    @GetMapping("/active")
    fun getActiveEdition(): Edition? = service.getActiveEdition()

    /**
     * Returns all editions that are currently inactive.
     */
    @GetMapping("/inactive")
    fun getInactiveEditions(): Iterable<Edition> = service.getInactiveEditions()

    /**
     * Activate the given [edition]. If this [edition] does not exist yet, it will be automatically created.
     * Returns a 400 (BAD REQUEST) if the edition is already active,
     * or a 403 (FORBIDDEN) if there is already another active edition.
     */
    @PostMapping("/{edition}/activate")
    fun makeEditionActive(@PathVariable edition: String) = service.makeEditionActive(edition)

    /**
     * Inactivate the given [edition]. Returns a 400 (BAD REQUEST) if the [edition] is already inactive.
     */
    @PostMapping("/{edition}/inactivate")
    fun makeEditionInactive(@PathVariable edition: String) = service.makeEditionInactive(edition)
}