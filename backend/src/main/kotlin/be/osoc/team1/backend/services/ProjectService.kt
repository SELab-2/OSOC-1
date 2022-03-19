package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.repositories.ProjectRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProjectService(private val repository: ProjectRepository) {
    /**
     * Get all projects
     */
    fun getAllProjects(): Iterable<Project> = repository.findAll()

    /**
     * Get a project by its [id], if this id doesn't exist throw an InvalidProjectIdException
     */
    fun getProjectById(id: UUID): Project = repository.findByIdOrNull(id) ?: throw InvalidProjectIdException()

    /**
     * Deletes a project by its [id], if this id doesn't exist throw an InvalidProjectIdException
     */
    fun deleteProjectById(id: UUID) {
        if (!repository.existsById(id))
            throw InvalidProjectIdException()

        repository.deleteById(id)
    }

    /**
     * Creates a new project based on [project]
     */
    fun postProject(project: Project): UUID {
        return repository.save(project).id
    }

    /**
     * Updates a project based on [project], if [project] is not in [repository] throw InvalidProjectIdException
     */
    fun patchProject(project: Project) {
        if (!repository.existsById(project.id))
            throw InvalidProjectIdException()

        repository.save(project)
    }

    /**
     * Adds a student to project based on [projectId], if [projectId] is not in [repository] throw InvalidProjectIdException
     */
    fun addStudentToProject(projectId: UUID, student: Student) {
        val project: Project = getProjectById(projectId)
        project.students.add(student)
        repository.save(project)
    }

    /**
     * removes a student from project based on [projectId] and [studentId],
     * if [projectId] is not in [repository] throw InvalidProjectIdException
     * if [studentId] not assigned to project throw FailedOperationException
     */
    fun removeStudentFromProject(projectId: UUID, studentId: UUID) {
        val project: Project = getProjectById(projectId)
        if (!project.students.removeIf { it.id == studentId }) {
            throw FailedOperationException("Given student is not assigned to project")
        }
        repository.save(project)
    }

    /**
     * Adds a coach to project based on [projectId],
     * if [projectId] is not in [repository] throw InvalidProjectIdException
     */
    fun addCoachToProject(projectId: UUID, coach: User) {
        val project: Project = getProjectById(projectId)
        project.coaches.add(coach)
        repository.save(project)
    }

    /**
     * removes a coach from project based on [projectId] and [coachId],
     * if [projectId] is not in [repository] throw InvalidProjectIdException
     * if [coachId] not assigned to project throw FailedOperationException
     */
    fun removeCoachFromProject(projectId: UUID, coachId: UUID) {
        val project: Project = getProjectById(projectId)
        if (!project.coaches.removeIf { it.id == coachId }) {
            throw FailedOperationException("Given coach is not assigned to project")
        }
        repository.save(project)
    }
}
