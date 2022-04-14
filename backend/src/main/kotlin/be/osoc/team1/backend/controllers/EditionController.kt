package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.ActiveEdition
import be.osoc.team1.backend.services.EditionService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/{edition}")
class EditionController(val service: EditionService) {

    /**
     * Inactivate the given [edition]. Returns a 400 (BAD REQUEST) if the [edition] was already inactive.
     */
    @PostMapping("/inactivate")
    fun makeEditionInactive(@PathVariable edition: String) = service.makeEditionInactive(ActiveEdition(edition))

    /**
     * Activate the given [edition]. Returns a 400 (BAD REQUEST) if the edition was already active,
     * or a 403 (FORBIDDEN) if there is already another active edition.
     */
    @PostMapping("/activate")
    fun makeEditionActive(@PathVariable edition: String) = service.makeEditionActive(ActiveEdition(edition))
}