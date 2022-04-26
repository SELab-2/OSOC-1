package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.services.PagedCollection
import be.osoc.team1.backend.services.Pager
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder
import java.util.UUID

@RestController
@RequestMapping("/{edition}/projects")
class ProjectController(private val service: ProjectService) {

    /**
     * Get all projects that are a part of the given OSOC [edition].
     * The results can also be filtered by [name] (default value is empty so no project is excluded).
     */
    @GetMapping
    @Secured("ROLE_COACH")
    fun getAllProjects(
        @RequestParam(defaultValue = "") name: String,
        @PathVariable edition: String,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "50") pageSize: Int
    ): PagedCollection<Project> {
        val decodedName = URLDecoder.decode(name, "UTF-8")
        return service.getAllProjects(edition, decodedName).page(Pager(pageNumber, pageSize))
    }

    /**
     * Get a project by its [projectId]. If there is no project with the given [projectId] and [edition],
     * return a 404 (NOT FOUND).
     */
    @GetMapping("/{projectId}")
    @Secured("ROLE_COACH")
    fun getProjectById(@PathVariable projectId: UUID, @PathVariable edition: String): Project =
        service.getProjectById(projectId, edition)

    /**
     * Deletes a project with its [projectId]. If there is no project with the given [projectId] and [edition],
     * return a 404 (NOT FOUND).
     */
    @DeleteMapping("/{projectId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun deleteProjectById(@PathVariable projectId: UUID, @PathVariable edition: String) =
        service.deleteProjectById(projectId, edition)

    /**
     * Creates a project from the request body, this can also override an already existing project.
     * Returns the created project in the response body, with a link pointing to the resource in the Location header.
     */
    @PostMapping
    @Secured("ROLE_ADMIN")
    fun postProject(
        @RequestBody projectRegistration: Project,
        @PathVariable edition: String
    ): ResponseEntity<Project> {
        val project = Project(
            projectRegistration.name, projectRegistration.description, projectRegistration.clientName,
            edition,
            projectRegistration.coaches, projectRegistration.positions, projectRegistration.assignments
        )
        val createdProject = service.postProject(project)
        return getObjectCreatedResponse(createdProject.id, createdProject)
    }

    /**
     * Patches a project by updating the project with [projectId] to match [project].
     * Throws an exception if the path [projectId] does not match the id of [project]. If the project we attempt to
     * patch a non-existent project a 404 not found is returned. If we attempt to change the edition of a project then a
     * 403 forbidden is returned.
     *
     * Returns the patched project in the response body, with a link pointing to the resource in the Location header.
     *
     * The endpoint expects every field in full except for the assignment list, the assignment list should be provided
     * as a list of urls that if fetched return associated the [Assignment] object.
     *
     * Example of the assignment field:
     * ```json
     * "assignments": [
     *     "http://localhost:8080/api/assignments/f9fefa13-80a3-439a-bf59-3cdacd60e2fc",
     *     "http://localhost:8080/api/assignments/6adf6cb3-6638-4771-a281-403c0bf3427a"
     * ],
     * ```
     */
    @PatchMapping("/{projectId}")
    @Secured("ROLE_ADMIN")
    fun patchProject(
        @PathVariable projectId: UUID,
        @PathVariable edition: String,
        @RequestBody project: Project
    ): ResponseEntity<Project> {
        if (projectId != project.id)
            throw FailedOperationException("Request url id=\"$projectId\" did not match request body id=\"${project.id}\"")

        val updatedProject = service.patchProject(project, edition)
        return getObjectCreatedResponse(updatedProject.id, updatedProject, HttpStatus.OK)
    }

    /**
     * Gets all students assigned to a project. If there is no project with the given [projectId] and [edition],
     * return a 404 (NOT FOUND).
     */
    @GetMapping("/{projectId}/students")
    @Secured("ROLE_COACH")
    fun getStudentsOfProject(@PathVariable projectId: UUID, @PathVariable edition: String): Collection<Student> =
        service.getStudents(projectId, edition)

    /**
     * Gets all coaches of a project. If there is no project with the given [projectId] and [edition],
     * return a 404 (NOT FOUND).
     */
    @GetMapping("/{projectId}/coaches")
    @Secured("ROLE_COACH")
    fun getCoachesOfProject(@PathVariable projectId: UUID, @PathVariable edition: String): Collection<User> =
        service.getProjectById(projectId, edition).coaches

    /**
     * assign a coach to a project. If a project with [projectId] and [edition] or a user with [coachId]
     * doesn't exist the service will return a 404.
     */
    @PostMapping("/{projectId}/coaches")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun postCoachToProject(@PathVariable projectId: UUID, @RequestBody coachId: UUID, @PathVariable edition: String) =
        service.addCoachToProject(projectId, coachId, edition)

    /**
     * Delete the coach identified by [coachId] from the project identified by [projectId].
     * If there is no project with the given [projectId] and [edition], or the coach doesn't exist,
     * the service will return a 404 (NOT FOUND).
     */
    @DeleteMapping("/{projectId}/coaches/{coachId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun deleteCoachFromProject(@PathVariable projectId: UUID, @PathVariable coachId: UUID, @PathVariable edition: String) =
        service.removeCoachFromProject(projectId, coachId, edition)

    /**
     * Returns conflicts of students being assigned to multiple projects, format:
     * ```
     * [
     *     {
     *         "student": "(STUDENT 1 URL)",
     *         "projects": ["(PROJECT 1 URL)", "(PROJECT 2 URL)"]
     *     },
     *     {
     *         "student": "(STUDENT 2 URL)",
     *         "projects": ["(PROJECT 1 URL)", "(PROJECT 2  URL)"]
     *     }
     * ]
     * ```
     */
    @GetMapping("/conflicts")
    @Secured("ROLE_COACH")
    fun getProjectConflicts(@PathVariable edition: String): MutableList<ProjectService.Conflict> =
        service.getConflicts(edition)

    /**
     * Assigns a student to a position on the project, format:
     * ```
     * {
     *     "student": "STUDENT(Student) ID",
     *     "position": "POSITION(Position) ID",
     *     "suggester": "SUGGESTER(User) ID",
     *     "reason": "REASON(String)"
     * }
     * ```
     * Will return a 404 if any of the ids are invalid. Will throw a 403 if the required conditions for assignment are
     * not met. These conditions are described in the documentation for [ProjectService.postAssignment].
     */
    @PostMapping("/{projectId}/assignments")
    @Secured("ROLE_COACH")
    fun postAssignment(
        @PathVariable projectId: UUID,
        @RequestBody assignment: ProjectService.AssignmentPost,
        @PathVariable edition: String
    ) =
        service.postAssignment(projectId, assignment, edition)

    /**
     * Removes assignment with [assignmentId] of a student to a position on the project identified with the given
     * [projectId] and [edition]. Will return a 404 if either the project or the assignment don't exist or
     * if there is no assignment with that [assignmentId] which is a part of the project.
     */
    @DeleteMapping("/{projectId}/assignments/{assignmentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_COACH")
    fun deleteAssignment(@PathVariable projectId: UUID, @PathVariable assignmentId: UUID, @PathVariable edition: String) =
        service.deleteAssignment(projectId, assignmentId, edition)
}
