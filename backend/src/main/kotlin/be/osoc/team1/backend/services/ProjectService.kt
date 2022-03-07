package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Coach
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
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

    /**
     * Adds a student to project based on [projId], if [projId] is not in [repository] throw InvalidIdException
     */
    fun addStudentToProject(projId: UUID, stud: Student) {
        val project: Project = getProjectById(projId)
        project.students.add(stud)
        repository.save(project)
    }

    /**
     * removes a student from project based on [projId] and [studId], if [projId] is not in [repository]
     * or [studId] not in project throw InvalidIdException
     */
    fun removeStudentFromProject(projId: UUID, studId: UUID) {
        val project: Project = getProjectById(projId)
        val s = project.students.size
        project.students.filter { it.id != studId }
        if (project.students.size == s) {
            throw InvalidIdException()
        }
        repository.save(project)
    }

    /**
     * Adds a coach to project based on [projId], if [projId] is not in [repository] throw InvalidIdException
     */
    fun addCoachToProject(projId: UUID, coach: Coach) {
        val project: Project = getProjectById(projId)
        project.coaches.add(coach)
        repository.save(project)
    }

    /**
     * removes a coach from project based on [projId] and [coachId], if [projId] is not in [repository]
     * or [coachId] not in project throw InvalidIdException
     */
    fun removeCoachFromProject(projId: UUID, coachId: UUID) {
        val project: Project = getProjectById(projId)
        val s = project.coaches.size
        project.coaches.filter { it.id != coachId }
        if (project.coaches.size == s) {
            throw InvalidIdException()
        }
        repository.save(project)
    }
}