package be.osoc.team1.backend.services

import InvalidRoleRequirementIdException
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.RoleRequirement
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.repositories.RoleRequirementRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProjectService(private val repository: ProjectRepository, private val roleRepository: RoleRequirementRepository) {
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

    /**
     * Gets conflicts (a conflict involves a student being assigned to 2 projects at the same time)
     */
    fun getConflicts(): MutableList<Conflict> {
        val studentsMap = mutableMapOf<UUID, MutableList<UUID>>()
        for (project in getAllProjects()) {
            for (student in project.students) {
                // add project id to map with student as key
                studentsMap.putIfAbsent(student.id, mutableListOf())
                studentsMap[student.id]?.add(project.id)
            }
        }
        val conflicts = mutableListOf<Conflict>()
        for ((studentId, projectIds) in studentsMap.entries) {
            if (projectIds.size > 1) {
                // this student has a conflict
                conflicts.add(Conflict(studentId, projectIds))
            }
        }
        return conflicts
    }

    fun getRoleRequirementById(roleId: UUID): RoleRequirement = roleRepository.findByIdOrNull(roleId)
        ?: throw InvalidRoleRequirementIdException("Role not found")

    fun assignStudentToRole(student: Student, roleId: UUID, projectId: UUID) {
        if (getProjectById(projectId).requiredRoles.find { it.id == roleId } == null)
            throw InvalidRoleRequirementIdException("The specified role is not part of the specified project.")

        for (requiredRole in getProjectById(projectId).requiredRoles) {
            if (requiredRole.assignees.contains(student))
                throw ForbiddenOperationException("This student was already assigned a role on this project!")
        }

        val role = getRoleRequirementById(roleId)
        role.assign(student)
        roleRepository.save(role)
    }

    fun removeStudentFromRole(student: Student, roleId: UUID, projectId: UUID) {
        if (getProjectById(projectId).requiredRoles.find { it.id == roleId } == null)
            throw InvalidRoleRequirementIdException("The specified role is not part of the specified project.")

        val role = getRoleRequirementById(roleId)
        role.remove(student)
        roleRepository.save(role)
    }

    data class Conflict(val student: UUID, val projects: MutableList<UUID> = mutableListOf())
}
