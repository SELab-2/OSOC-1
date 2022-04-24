package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidEditionIdException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.EditionRepository
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class EditionService(
    private val repository: EditionRepository,
    private val studentService: StudentService,
    private val projectService: ProjectService,
    private val userService: UserService
) {

    fun getEdition(editionName: String): Edition = repository.findByIdOrNull(editionName)
        ?: throw InvalidEditionIdException()

    fun getActiveEdition(): Edition? = repository.findAll().firstOrNull(Edition::isActive)

    fun getInactiveEditions(): Iterable<Edition> = repository.findAll().filterNot(Edition::isActive)

    /**
     * Given a currently inactive [Edition] identified by the given [editionName], make it active.
     * If there is no [Edition] with that [editionName], it will be created automatically.
     * Throws a [FailedOperationException] if the edition is already active, or a
     * [ForbiddenOperationException] if there is already another active edition.
     */
    fun makeEditionActive(editionName: String) {
        val edition = repository.findByIdOrNull(editionName) ?: Edition(editionName, false)
        if (edition.isActive) {
            throw FailedOperationException("The given edition is already active.")
        }
        if (repository.findAll().count(Edition::isActive) == 1) {
            throw ForbiddenOperationException("There can only be one active edition at a time.")
        }
        edition.isActive = true
        repository.save(edition)
    }

    /**
     * Given a currently active [Edition] identified by the given [editionName], make it inactive.
     * If there is no [Edition] with that [editionName], it will be created automatically.
     * If the edition did exist, this deactivates all [Role.Coach] accounts, by making them [Role.Disabled].
     * Throws a [FailedOperationException] if the edition is already inactive.
     */
    fun makeEditionInactive(editionName: String) {
        var edition = repository.findByIdOrNull(editionName)
        val editionExists = edition != null
        if (!editionExists) {
            edition = Edition(editionName, true)
        }
        if (!edition!!.isActive) {
            throw FailedOperationException("The given edition is already inactive.")
        }
        edition.isActive = false
        if (editionExists) {
            userService.getAllUsers()
                .filter { it.role == Role.Coach }
                .forEach { userService.changeRole(it.id, Role.Disabled) }
        }
        repository.save(edition)
    }

    /**
     * Deletes the [Edition] identified by the given [editionName] from the database.
     * This function also deletes all of the data associated with this edition.
     * Throws an [InvalidIdException] if the edition does not exist.
     */
    fun deleteEdition(editionName: String) {
        if (!repository.existsById(editionName)) {
            throw InvalidEditionIdException()
        }
        studentService.getAllStudents(Sort.unsorted(), editionName)
            .forEach { studentService.deleteStudentById(it.id) }
        projectService.getAllProjects(editionName)
            .forEach { projectService.deleteProjectById(it.id, editionName) }
        repository.deleteById(editionName)
    }
}
