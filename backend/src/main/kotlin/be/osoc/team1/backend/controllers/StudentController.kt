package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.StudentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class StudentController(private val service: StudentService) {

    /**
     * Get a list of all students in the database. This request cannot fail.
     */
    @GetMapping("/students")
    fun getAllStudents() = service.getAllStudents()

    /**
     * Returns the student with the corresponding [id]. If no such student exists,
     * returns a "404: Not Found" message instead.
     */
    @GetMapping("/students/{id}")
    fun getStudentById(@PathVariable id: UUID): Student {
        try {
            return service.getStudentById(id)
        } catch (_: InvalidIdException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No student found with given id")
        }
    }

    /**
     * Deletes the student with the corresponding [id]. If no such student exists,
     * returns a "404: Not Found" message instead.
     */
    @DeleteMapping("/students/{id}")
    fun deleteStudentById(@PathVariable id: UUID) {
        try {
            service.deleteStudentById(id)
        } catch (_: InvalidIdException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No student found with given id")
        }
    }

    /**
     * Add a student to the database. The student should be passed in the request body
     * as a JSON object and should have the following format:
     *
     * {
     *     "id": "(INSERT ANY VALID UUID)",
     *     "firstName": "(INSERT FIRST NAME)",
     *     "lastName": "(INSERT LAST NAME)"
     * }
     *
     * The id can be any UUID, the database will simply ignore it and choose another random
     * UUID for this student. TODO: find a way to remove the need to send an id
     * The chosen id is then returned to the API caller. This request cannot fail,
     * which implies that no checking is done to see if firstName or lastName qualify as valid 'names'.
     * This verification is the responsibility of the caller.
     */
    @PutMapping("/students/create")
    fun putStudent(@RequestBody student: Student) = service.putStudent(student)
}
