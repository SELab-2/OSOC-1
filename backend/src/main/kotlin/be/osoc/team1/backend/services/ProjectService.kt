package be.osoc.team1.backend.services

import InvalidAssignmentIdException
import be.osoc.team1.backend.exceptions.InvalidPositionIdException
import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.repositories.ProjectRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProjectService(
    private val repository: ProjectRepository,
    private val assignmentRepository: AssignmentRepository,
    private val studentService: StudentService,
    private val userService: UserService
) {
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
     * Creates a new project based on [project]. Returns the created project.
     */
    fun postProject(project: Project): Project = repository.save(project)

    /**
     * Updates a project based on [project], if [project] is not in [repository] throw InvalidProjectIdException
     */
    fun patchProject(project: Project) {
        if (!repository.existsById(project.id))
            throw InvalidProjectIdException()

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

    fun getStudents(projectId: UUID): List<Student> {
        return getStudents(getProjectById(projectId))
    }

    fun getStudents(project: Project): List<Student> {
        val students = mutableListOf<Student>()
        for (assignment in project.assignments) {
            students.add(assignment.student)
        }
        return students
    }

    /**
     * Gets conflicts (a conflict involves a student being assigned to 2 projects at the same time)
     */
    fun getConflicts(): MutableList<Conflict> {
        val studentsMap = mutableMapOf<UUID, MutableList<UUID>>()
        for (project in getAllProjects()) {
            for (student in getStudents(project)) {
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

    /**
     * Get an assignment by its [assignmentId], if it doesn't exist a [InvalidAssignmentIdException] will be thrown.
     */
    fun getAssignmentById(assignmentId: UUID): Assignment {
        return assignmentRepository.findByIdOrNull(assignmentId) ?: throw InvalidAssignmentIdException()
    }

    /**
     * Assigns a student to a specific position on the project with [projectId]. A [ForbiddenOperationException] will be
     * thrown if the student was already assigned on this project, if that position already has enough assignees or if
     * the specified student doesn't actually have the required skill to be given the specified position. A
     * [InvalidAssignmentIdException] will be thrown if specified position is not part of the specified project. If the
     * specified student or suggester don't exist then a corresponding [InvalidIdException] will be thrown.
     */
    fun postAssignment(projectId: UUID, assignmentForm: AssignmentPost) {
        val project = getProjectById(projectId)
        val position = project.positions.find { it.id == assignmentForm.position }
            ?: throw InvalidPositionIdException("The specified position is not part of the specified project.")

        if (project.assignments.find { it.student.id == assignmentForm.student } != null)
            throw ForbiddenOperationException("This student was already assigned a position on this project!")

        val amount = project.assignments.count { it.position == position }
        if (amount >= position.amount)
            throw ForbiddenOperationException("This position already has enough assignees!")

        val student = studentService.getStudentById(assignmentForm.student)
        if (!student.skills.contains(position.skill))
            throw ForbiddenOperationException("This student doesn't have the required skill to be assigned to this position.")

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
    fun deleteAssignment(projectId: UUID, assignmentId: UUID) {
        val project = getProjectById(projectId)
        project.assignments.find { it.id == assignmentId }
            ?: throw InvalidAssignmentIdException("The specified assignment is not part of the specified project!")

        val assignment = getAssignmentById(assignmentId)
        project.assignments.remove(assignment)
        repository.save(project)
    }

    data class Conflict(val student: UUID, val projects: MutableList<UUID> = mutableListOf())
    data class AssignmentPost(val student: UUID, val position: UUID, val suggester: UUID, val reason: String)
}
