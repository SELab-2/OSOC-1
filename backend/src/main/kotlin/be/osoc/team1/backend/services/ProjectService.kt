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
import be.osoc.team1.backend.util.ProjectSerializer
import be.osoc.team1.backend.util.StudentSerializer
import org.springframework.stereotype.Service
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
    fun getAllProjects(edition: String, searchQuery: String = ""): List<Project> =
        repository.findByEdition(edition).filter { nameMatchesSearchQuery(it.name, searchQuery) }.toList()

    /**
     * Get a project by its [id]. If there is no project with the given [id] and [edition],
     * throw an [InvalidProjectIdException].
     */
    fun getProjectById(id: UUID, edition: String): Project =
        repository.findByIdAndEdition(id, edition) ?: throw InvalidProjectIdException()

    /**
     * Deletes a project by its [id]. If there is no project with the given [id] and [edition],
     * throw an [InvalidProjectIdException].
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
     * Updates a project based on [project]. If there is no project with the same id as the given project
     * and the given [edition], throw an [InvalidProjectIdException]. If an attempt is made to change the edition of the
     * project then a [ForbiddenOperationException] is thrown.
     */
    fun patchProject(project: Project, edition: String): Project {
        val oldProject = getProjectById(project.id, edition)
        if (oldProject.edition != project.edition)
            throw ForbiddenOperationException("The edition field cannot be changed!")

        return repository.save(project)
    }

    /**
     * Adds a coach to project based on [projectId].
     * If there is no project with the given [projectId] and [edition], throw an [InvalidProjectIdException].
     * If there is no user with [coachId] a [InvalidUserIdException] will be thrown.
     */
    fun addCoachToProject(projectId: UUID, coachId: UUID, edition: String) {
        val project = getProjectById(projectId, edition)
        val coach = userService.getUserById(coachId)
        project.coaches.add(coach)
        repository.save(project)
    }

    /**
     * removes a coach from project based on [projectId] and [coachId].
     * If there is no project with the given [projectId] and [edition], throw an [InvalidProjectIdException].
     * if [coachId] not assigned to project throw [FailedOperationException].
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
        val studentsMap = mutableMapOf<Student, MutableList<String>>()
        val projectSerializer = ProjectSerializer()
        for (project in getAllProjects(edition)) {
            for (student in getStudents(project)) {
                // add project id to map with student as key
                studentsMap.putIfAbsent(student, mutableListOf())
                studentsMap[student]!!.add(projectSerializer.toUrl(project))
            }
        }
        val conflicts = mutableListOf<Conflict>()
        val studentSerializer = StudentSerializer()
        for ((student, projectIds) in studentsMap.entries) {
            if (projectIds.size > 1) {
                // this student has a conflict
                conflicts.add(Conflict(studentSerializer.toUrl(student), projectIds))
            }
        }
        return conflicts
    }

    /**
     * Assigns a student to a specific position on the project identified with the given [projectId] and [edition].
     * A [ForbiddenOperationException] will be thrown if the student was already assigned this position.
     * An [InvalidAssignmentIdException] will be thrown if the position is not part of the the project.
     * If the project, student or suggester don't exist then a corresponding [InvalidIdException] will be thrown.
     */
    fun postAssignment(projectId: UUID, assignmentForm: AssignmentPost, edition: String) {
        val project = getProjectById(projectId, edition)
        val position = project.positions.find { it.id == assignmentForm.position }
            ?: throw InvalidPositionIdException("The specified position is not part of the specified project.")

        if (project.assignments.find { it.student.id == assignmentForm.student && position == it.position } != null)
            throw ForbiddenOperationException("This student was already assigned this position on the project!")

        val student = studentService.getStudentById(assignmentForm.student, edition)
        val suggester = userService.getUserById(assignmentForm.suggester)
        val assignment = Assignment(student, position, suggester, assignmentForm.reason, edition)
        project.assignments.add(assignment)
        repository.save(project)
    }

    /**
     * Unassign a student by removing the assignment with [assignmentId] from the project with [projectId]. If this
     * assignment is not part of the project, or it just outright doesn't exist then an [InvalidAssignmentIdException]
     * will be thrown. If there is no project with the given [projectId] and [edition], throw an [InvalidProjectIdException].
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
