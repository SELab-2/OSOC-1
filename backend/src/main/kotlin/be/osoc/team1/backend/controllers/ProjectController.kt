package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.services.ProjectService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/projects")
class ProjectController(private val service: ProjectService) {

    /** check list
     * post projects/id/student
     * delete projects/id/students/id
     * post projects/id/coaches
     * delete projects/id/coaches/id
     * get projects/conflicts
     */
    /**
     * Get all projects from service
     */
    @GetMapping("/")
    fun getAllProjects(): Iterable<Project> = service.getAllProjects()

    /**
     * Get a project by its [id], if this id doesn't exist the service will throw a InvalidIdException
     * which will be converted in to a 404
     */
    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: UUID): Project = service.getProjectById(id)

    /**
     * Deletes a project with its [id], if this id doesn't exist the service will throw a InvalidIdException
     * which will be converted in to a 404
     */
    @DeleteMapping("/{id}")
    fun deleteProjectById(@PathVariable id: UUID) = service.deleteProjectById(id)

    /**
     * Creates a project from the request body, this can also override an already existing project
     */
    @PutMapping
    fun putProject(@RequestBody proj: Project) = service.putProject(proj)

    @GetMapping("/{projId}/students")
    fun GetStudentsOfProject(@PathVariable projId: UUID): Collection<Student> {
        return service.getProjectById(projId).students
    }

    @PostMapping("/{projId}/students")
    fun postStudentToProject(@PathVariable projId: UUID, @RequestBody stud: Student) {
        service.addStudentToProject(projId, stud)
    }
}