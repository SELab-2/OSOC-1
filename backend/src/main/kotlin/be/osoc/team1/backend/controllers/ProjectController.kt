package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.services.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/projects")
class ProjectController(private val service: ProjectService) {

    /**
     * Get all projects from service
     */
    @GetMapping
    @Secured("ROLE_COACH")
    fun getAllProjects(): Iterable<Project> = service.getAllProjects()

    /**
     * Get a project by its [projectId], if this id doesn't exist the service will return a 404
     */
    @GetMapping("/{projectId}")
    @Secured("ROLE_COACH")
    fun getProjectById(@PathVariable projectId: UUID): Project = service.getProjectById(projectId)

    /**
     * Deletes a project with its [projectId], if this [projectId] doesn't exist the service will return a 404
     */
    @DeleteMapping("/{projectId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun deleteProjectById(@PathVariable projectId: UUID) = service.deleteProjectById(projectId)

    /**
     * Creates a project from the request body, this can also override an already existing project.
     * Returns the created project.
     */
    @PostMapping
    @Secured("ROLE_ADMIN")
    fun postProject(@RequestBody project: Project): ResponseEntity<Project> {
        val createdProject = service.postProject(project)
        return getObjectCreatedResponse(createdProject.id, createdProject)
    }

    /**
     * Gets all students assigned to a project, if this [projectId] doesn't exist the service will return a 404
     */
    @GetMapping("/{projectId}/students")
    @Secured("ROLE_COACH")
    fun getStudentsOfProject(@PathVariable projectId: UUID): Collection<Student> =
        service.getStudents(projectId)

    /**
     * Gets all coaches of a project, if this [projectId] doesn't exist the service will return a 404
     */
    @GetMapping("/{projectId}/coaches")
    @Secured("ROLE_COACH")
    fun getCoachesOfProject(@PathVariable projectId: UUID): Collection<User> =
        service.getProjectById(projectId).coaches

    /**
     * assign a coach to a project, if this [projectId] doesn't exist the service will return a 404
     */
    @PostMapping("/{projectId}/coaches")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun postCoachToProject(@PathVariable projectId: UUID, @RequestBody coach: User) =
        service.addCoachToProject(projectId, coach)

    /**
     * Deletes a coach [coachId] from a project [projectId], if [projectId] or [coachId] doesn't exist the service will return a 404
     */
    @DeleteMapping("/{projectId}/coaches/{coachId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun deleteCoachFromProject(@PathVariable projectId: UUID, @PathVariable coachId: UUID) =
        service.removeCoachFromProject(projectId, coachId)

    /**
     * Returns conflicts of students being assigned to multiple projects, format:
     * ```
     * [
     *     {
     *         "student": "(STUDENT 1 ID)",
     *         "projects": ["(PROJECT 1 ID)", "(PROJECT 2 ID)"]
     *     },
     *     {
     *         "student": "(STUDENT 2 ID)",
     *         "projects": ["(PROJECT 1 ID)", "(PROJECT 2 ID)"]
     *     }
     * ]
     * ```
     */
    @GetMapping("/conflicts")
    @Secured("ROLE_COACH")
    fun getProjectConflicts(): MutableList<ProjectService.Conflict> = service.getConflicts()

    /**
     * Assigns a student to a role in the project, format:
     * ```
     * {
     *     "student": "STUDENT(Student) ID",
     *     "role": "ROLE(RoleRequirement) ID",
     *     "suggester": "SUGGESTER(User) ID",
     *     "reason": "REASON(String)"
     * }
     * ```
     * Will return a 404 if any of the ids are invalid. Will throw a 403 if the required conditions for assignment are
     * not met. These conditions are described in the documentation for [ProjectService.postAssignment].
     */
    @PostMapping("/{projectId}/assignments")
    fun postAssignment(@PathVariable projectId: UUID, @RequestBody assignment: ProjectService.AssignmentPost) {
        service.postAssignment(projectId, assignment)
    }

    /**
     * Removes assignment with [assignmentId] of a student to a role on project with [projectId]. Will return a 404 if
     * the specified [assignmentId] is not actually part of this project or if [assignmentId] outright doesn't exist.
     */
    @DeleteMapping("/{projectId}/assignments/{assignmentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun deleteAssignment(@PathVariable projectId: UUID, @PathVariable assignmentId: UUID) {
        service.deleteAssignment(projectId, assignmentId)
    }
}
