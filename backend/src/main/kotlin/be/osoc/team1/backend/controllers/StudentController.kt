package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.services.StudentService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/students")
class StudentController(private val service: StudentService) {

    /**
     * Get a list of all students in the database. This request cannot fail.
     */
    @GetMapping("")
    fun getAllStudents() = service.getAllStudents()

    /**
     * Returns the student with the corresponding [id]. If no such student exists,
     * returns a "404: Not Found" message instead.
     */
    @GetMapping("/{id}")
    fun getStudentById(@PathVariable id: UUID) = service.getStudentById(id)


    /**
     * Deletes the student with the corresponding [id]. If no such student exists,
     * returns a "404: Not Found" message instead.
     */
    @DeleteMapping("/{id}")
    fun deleteStudentById(@PathVariable id: UUID) = service.deleteStudentById(id)

    /**
     * Add a student to the database. The student should be passed in the request body
     * as a JSON object and should have the following format:
     *
     * {
     *     "firstName": "(INSERT FIRST NAME)",
     *     "lastName": "(INSERT LAST NAME)"
     * }
     *
     * The id for this student chosen by the database is then returned to the API caller. This request cannot fail,
     * which implies that no checking is done to see if firstName or lastName qualify as valid 'names'.
     * This verification is the responsibility of the caller.
     */
    @PutMapping("")
    fun putStudent(@RequestBody student: Student) = service.putStudent(student)
}
