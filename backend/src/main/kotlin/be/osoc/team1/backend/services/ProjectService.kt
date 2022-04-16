package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidAssignmentIdException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.exceptions.InvalidPositionIdException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.exceptions.InvalidUserIdException
import be.osoc.team1.backend.repositories.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@Service
class ProjectService(
    private val repository: ProjectRepository,
    private val studentService: StudentService,
    private val userService: UserService
) {
    /**
     * Get all projects that are a part of the given OSOC [edition].
     * The projects can also be filtered by the optional [searchQuery] parameter.
     * See the documentation of the [nameMatchesSearchQuery] function to understand how the filtering is done.
     */
    fun getAllProjects(edition: String, searchQuery: String = ""): Iterable<Project> =
        repository.findByEdition(edition).filter { nameMatchesSearchQuery(it.name, searchQuery) }

    /**
     * Get a project by its [id], if this id doesn't exist throw an [InvalidProjectIdException]
     */
    fun getProjectById(id: UUID, edition: String): Project =
        repository.findByIdAndEdition(id, edition) ?: throw InvalidProjectIdException()

    /**
     * Deletes a project by its [id], if this id doesn't exist throw an [InvalidProjectIdException]
     */
    fun deleteProjectById(id: UUID, edition: String) {
        if (!repository.existsByIdAndEdition(id, edition))
            throw InvalidProjectIdException()

        repository.deleteById(id)
    }

    /**
     * Creates a new project based on [project]. Returns the created project.
     */
    fun postProject(project: Project): Project = repository.save(project)

    /**
     * Updates a project based on [project], if [project] is not in [repository] throw [InvalidProjectIdException]
     */
    fun patchProject(project: Project, edition: String) {
        if (!repository.existsByIdAndEdition(project.id, edition))
            throw InvalidProjectIdException()

        repository.save(project)
    }

    /**
     * Adds a coach to project based on [projectId],
     * if [projectId] is not in [repository] throw [InvalidProjectIdException]
     * If there is no user with [coachId] a [InvalidUserIdException] will be thrown.
     */
    fun addCoachToProject(projectId: UUID, coachId: UUID, edition: String) {
        val project = getProjectById(projectId, edition)
        val coach = userService.getUserById(coachId)
        project.coaches.add(coach)
        repository.save(project)
    }

    /**
     * removes a coach from project based on [projectId] and [coachId],
     * if [projectId] is not in [repository] throw [InvalidProjectIdException]
     * if [coachId] not assigned to project throw [FailedOperationException]
     */
    fun removeCoachFromProject(projectId: UUID, coachId: UUID, edition: String) {
        val project: Project = getProjectById(projectId, edition)
        if (!project.coaches.removeIf { it.id == coachId }) {
            throw FailedOperationException("Given coach is not assigned to project")
        }
        repository.save(project)
    }

    fun getStudents(projectId: UUID, edition: String): List<Student> = getStudents(getProjectById(projectId, edition))

    fun getStudents(project: Project): List<Student> = project.assignments.map(Assignment::student).distinct()

    /**
     * Gets conflicts (a conflict involves a student being assigned to 2 projects at the same time)
     */
    fun getConflicts(edition: String): MutableList<Conflict> {
        val baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        val studentsMap = mutableMapOf<UUID, MutableList<String>>()
        for (project in getAllProjects(edition)) {
            for (student in getStudents(project)) {
                // add project id to map with student as key
                studentsMap.putIfAbsent(student.id, mutableListOf())
                studentsMap[student.id]!!.add("$baseUrl/projects/" + project.id)
            }
        }
        val conflicts = mutableListOf<Conflict>()
        for ((studentId, projectIds) in studentsMap.entries) {
            if (projectIds.size > 1) {
                // this student has a conflict
                conflicts.add(Conflict("$baseUrl/students/$studentId", projectIds))
            }
        }
        return conflicts
    }

    /**
     * Assigns a student to a specific position on the project with [projectId]. A [ForbiddenOperationException] will be
     * thrown if the student was already assigned this position. A
     * [InvalidAssignmentIdException] will be thrown if specified position is not part of the specified project. If the
     * specified student or suggester don't exist then a corresponding [InvalidIdException] will be thrown.
     */
    fun postAssignment(projectId: UUID, assignmentForm: AssignmentPost, edition: String) {
        val project = getProjectById(projectId, edition)
        val position = project.positions.find { it.id == assignmentForm.position }
            ?: throw InvalidPositionIdException("The specified position is not part of the specified project.")

        if (project.assignments.find { it.student.id == assignmentForm.student && position == it.position } != null)
            throw ForbiddenOperationException("This student was already assigned this position on the project!")

        val student = studentService.getStudentById(assignmentForm.student)
        val suggester = userService.getUserById(assignmentForm.suggester)
        val assignment = Assignment(student, position, suggester, assignmentForm.reason)
        project.assignments.add(assignment)
        repository.save(project)
    }

    /**
     * Unassign a student by removing the assignment with [assignmentId] from the project with [projectId]. If this
     * assignment is not part of the project, or it just outright doesn't exist then an [InvalidAssignmentIdException]
     * will be thrown.
     */
    fun deleteAssignment(projectId: UUID, assignmentId: UUID, edition: String) {
        val project = getProjectById(projectId, edition)
        val assignment = project.assignments.find { it.id == assignmentId }
            ?: throw InvalidAssignmentIdException("The specified assignment is not part of the specified project!")

        project.assignments.remove(assignment)
        repository.save(project)
    }

    data class Conflict(val student: String, val projects: MutableList<String> = mutableListOf())
    data class AssignmentPost(val student: UUID, val position: UUID, val suggester: UUID, val reason: String)
}
