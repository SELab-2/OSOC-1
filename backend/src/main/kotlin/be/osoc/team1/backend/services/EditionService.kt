package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.ActiveEdition
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.repositories.EditionRepository
import org.springframework.stereotype.Service

@Service
class EditionService(val repository: EditionRepository) {
    /**
     * Given a currently active [edition] present in the database, make it inactive by removing
     * it from the saved [ActiveEdition]s. Throws a [FailedOperationException] if the given
     * [edition] is already inactive (and thus not present in the database).
     */
    fun makeEditionInactive(edition: ActiveEdition) {
        if (!repository.existsById(edition.name)) {
            throw FailedOperationException("The given edition is already inactive.")
        }
        repository.delete(edition)
    }

    /**
     * Given a currently inactive [edition] not present in the database, make it active by adding
     * it to the saved [ActiveEdition]s. Throws a [FailedOperationException] if the given
     * [edition] is already active (and thus present in the database).
     * Throws a [ForbiddenOperationException] if there is already another active edition in the database.
     */
    fun makeEditionActive(edition: ActiveEdition) {
        if (repository.existsById(edition.name)) {
            throw FailedOperationException("The given edition is already active.")
        }
        if (repository.findAll().count() == 1) {
            throw ForbiddenOperationException("There can only be one active edition at a time.")
        }
        repository.save(edition)
    }
}