package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Coach
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/projects")
class ProjectController(private val service: ProjectService, @Autowired private val studentService: StudentService) {

    /**
     * Get all projects from service
     */
    @GetMapping
    fun getAllProjects(): Iterable<Project> = service.getAllProjects()

    /**
     * Get a project by its [projectId], if this id doesn't exist the service will return a 404
     */
    @GetMapping("/{projectId}")
    fun getProjectById(@PathVariable projectId: UUID): Project = service.getProjectById(projectId)

    /**
     * Deletes a project with its [projectId], if this [projectId] doesn't exist the service will return a 404
     */
    @DeleteMapping("/{projectId}")
    fun deleteProjectById(@PathVariable projectId: UUID) = service.deleteProjectById(projectId)

    /**
     * Creates a project from the request body, this can also override an already existing project
     * returns the id of the project
     */
    @PostMapping
    fun postProject(@RequestBody project: Project): UUID = service.postProject(project)

    /**
     * Gets all students assigned to a project, if this [projectId] doesn't exist the service will return a 404
     */
    @GetMapping("/{projectId}/students")
    fun getStudentsOfProject(@PathVariable projectId: UUID): Collection<Student> = service.getProjectById(projectId).students

    /**
     * Assign a student to a project, [studentId] is placed in the request body,
     * if this [projectId] doesn't exist the service will return a 404
     */
    @PostMapping("/{projectId}/students")
    fun postStudentToProject(@PathVariable projectId: UUID, @RequestBody studentId: UUID) {
        val student = studentService.getStudentById(studentId)
        service.addStudentToProject(projectId, student)
    }

    /**
     * Deletes a student [studentId] from a project [projectId], if [projectId] or [studentId] doesn't exist the service will return a 404
     */
    @DeleteMapping("/{projectId}/students/{studentId}")
    fun deleteStudentFromProject(@PathVariable projectId: UUID, @PathVariable studentId: UUID) =
        service.removeStudentFromProject(projectId, studentId)

    /**
     * Gets all coaches of a project, if this [projectId] doesn't exist the service will return a 404
     */
    @GetMapping("/{projectId}/coaches")
    fun getCoachesOfProject(@PathVariable projectId: UUID): Collection<Coach> = service.getProjectById(projectId).coaches

    /**
     * assign a coach to a project, if this [projectId] doesn't exist the service will return a 404
     */
    @PostMapping("/{projectId}/coaches")
    fun postCoachToProject(@PathVariable projectId: UUID, @RequestBody coach: Coach) =
        service.addCoachToProject(projectId, coach)

    /**
     * Deletes a coach [coachId] from a project [projectId], if [projectId] or [coachId] doesn't exist the service will return a 404
     */
    @DeleteMapping("/{projectId}/coaches/{coachId}")
    fun deleteCoachFromProject(@PathVariable projectId: UUID, @PathVariable coachId: UUID) =
        service.removeCoachFromProject(projectId, coachId)
}
