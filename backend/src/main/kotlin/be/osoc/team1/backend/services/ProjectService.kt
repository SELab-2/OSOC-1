package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.ProjectRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectService(private val repository: ProjectRepository) {
    /**
     * Get all projects
     */
    fun getAllProjects(): Iterable<Project> {
        return repository.findAll()
    }

    /**
     * Get a project by its [id], if this id doesn't exist throw an InvalidIdException
     */
    fun getProjectById(id: UUID): Project {
        return repository.findByIdOrNull(id) ?: throw InvalidIdException()
    }

    /**
     * Deletes a project by its [id], if this id doesn't exist throw an InvalidIdException
     */
    fun deleteProjectById(id: UUID) {
        if (!repository.existsById(id))
            throw InvalidIdException()

        repository.deleteById(id)
    }

    /**
     * Creates a new project based on [proj]
     */
    fun putProject(proj: Project) {
        repository.save(proj)
    }

    /**
     * Updates a project based on [proj], if [proj] is not in [repository] throw InvalidIdException
     */
    fun patchProject(proj: Project) {
        if (!repository.existsById(proj.id))
            throw InvalidIdException()

        repository.save(proj)
    }
}