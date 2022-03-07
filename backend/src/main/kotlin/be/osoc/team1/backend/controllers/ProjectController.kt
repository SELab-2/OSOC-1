package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Coach
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
     * post projects/id/coaches
     * delete projects/id/coaches/id
     * get projects/conflicts
     */
    /**
     * Get all projects from service
     */
    @GetMapping
    fun getAllProjects(): Iterable<Project> = service.getAllProjects()

    /**
     * Get a project by its [id], if this id doesn't exist the service will throw a InvalidIdException
     * which will be converted in to a 404
     */
    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: UUID): Project = service.getProjectById(id)

    /**
     * Deletes a project with its [id], if this [id] doesn't exist the service will throw a InvalidIdException
     * which will be converted in to a 404
     */
    @DeleteMapping("/{id}")
    fun deleteProjectById(@PathVariable id: UUID) = service.deleteProjectById(id)

    /**
     * Creates a project from the request body, this can also override an already existing project
     */
    @PutMapping
    fun putProject(@RequestBody proj: Project) = service.putProject(proj)

    /**
     * Gets all students of a project, if this [projId] doesn't exist the service will throw an InvalidIdException
     * which will be converted in to a 404
     */
    @GetMapping("/{projId}/students")
    fun GetStudentsOfProject(@PathVariable projId: UUID): Collection<Student> {
        return service.getProjectById(projId).students
    }

    /**
     * Post a student to a project, if this [projId] doesn't exist the service will throw an InvalidIdException
     * which will be converted in to a 404
     */
    @PostMapping("/{projId}/students")
    fun postStudentToProject(@PathVariable projId: UUID, @RequestBody stud: Student) {
        service.addStudentToProject(projId, stud)
    }

    /**
     * Deletes a student [studId] from a project [projId], if [projId] or [studId] doesn't exist the service will throw an InvalidIdException
     * which will be converted in to a 404
     */
    @DeleteMapping("/{projId}/students/{studId}")
    fun deleteStudentFromProject(@PathVariable projId: UUID, @PathVariable studId: UUID) {
        service.removeStudentFromProject(projId, studId)
    }

    /**
     * Gets all coaches of a project, if this [projId] doesn't exist the service will throw an InvalidIdException
     * which will be converted in to a 404
     */
    @GetMapping("/{projId}/coaches")
    fun GetCoachesOfProject(@PathVariable projId: UUID): Collection<Coach> {
        return service.getProjectById(projId).coaches
    }

    /**
     * Post a coach to a project, if this [projId] doesn't exist the service will throw an InvalidIdException
     * which will be converted in to a 404
     */
    @PostMapping("/{projId}/coaches")
    fun postCoachToProject(@PathVariable projId: UUID, @RequestBody coach: Coach) {
        service.addCoachToProject(projId, coach)
    }

    /**
     * Deletes a coach [coachId] from a project [projId], if [projId] or [coachId] doesn't exist the service will throw an InvalidIdException
     * which will be converted in to a 404
     */
    @DeleteMapping("/{projId}/coaches/{coachId}")
    fun deleteCoachFromProject(@PathVariable projId: UUID, @PathVariable coachId: UUID) {
        service.removeCoachFromProject(projId, coachId)
    }

    /**
     * Get conflicts of students
     */
    @GetMapping("/conflicts")
    fun getProjectConflicts() {
        val projectList = service.getAllProjects()
        val studentsMap = mutableMapOf<Student, MutableCollection<Project>>()
        for (project in projectList) {
            for (student in project.students) {
                if (studentsMap.containsKey(student)) {
                    studentsMap[student]!!.add(project)
                } else {
                    studentsMap[student] = mutableListOf(project)
                }
            }
        }
        for (student in studentsMap.keys) {
            if (studentsMap[student]!!.size > 1) {
                // this student has a conflict
            }
        }
    }
}