package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.services.StudentService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/students")
class StudentController(private val service: StudentService) {

    /**
     * Get a list of all students in the database. This request cannot fail.
     */
    @GetMapping
    fun getAllStudents(): Iterable<Student> = service.getAllStudents()

    /**
     * Returns the student with the corresponding [id]. If no such student exists,
     * returns a "404: Not Found" message instead.
     */
    @GetMapping("/{id}")
    fun getStudentById(@PathVariable id: UUID): Student = service.getStudentById(id)

    /**
     * Deletes the student with the corresponding [id]. If no such student exists,
     * returns a "404: Not Found" message instead.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun deleteStudentById(@PathVariable id: UUID) = service.deleteStudentById(id)

    /**
     * Add a student to the database. The student should be passed in the request body
     * as a JSON object and should have the following format:
     *
     * ```
     * {
     *     "firstName": "(INSERT FIRST NAME)",
     *     "lastName": "(INSERT LAST NAME)"
     * }
     * ```
     *
     * The location of the newly created student is then returned to the API caller in the location header.
     * No checking is done to see if firstName or lastName qualify as valid 'names'.
     * This verification is the responsibility of the caller.
     */
    @PostMapping
    fun addStudent(@RequestBody student: Student): ResponseEntity<Void> {
        val id = service.addStudent(student)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUriString()
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, location).build()
    }

    /**
     * Set the [status] of the student with the given [id]. If no such student exists,
     * returns a "404: Not Found" message instead. The [status] should be passed in the request body
     * as a JSON string and can have the following values:
     *
     * "Yes" for [StatusEnum.Yes],
     *
     * "Maybe" for [StatusEnum.Maybe],
     *
     * "No" for [StatusEnum.No] and
     *
     * "Undecided" for [StatusEnum.Undecided]
     *
     * Any other input value will result in a "400: Bad Request" response. These values are also case-sensitive.
     */
    @PostMapping("/{id}/status")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun setStudentStatus(@PathVariable id: UUID, @RequestBody status: StatusEnum) = service.setStudentStatus(id, status)

    /**
     * Add a [statusSuggestion] to the student with the given [id]. If no such student exists,
     * returns a "404: Not Found" message instead. The [statusSuggestion] should be passed in the request body
     * as a JSON object and should have the following format:
     *
     * ```
     * {
     *      "status": "Yes" OR "Maybe" OR "No",
     *      "motivation": "(INSERT MOTIVATION)"
     * }
     *```
     *
     * Any other values for the status will result in a "400: Bad Request" response.
     * Importantly, this includes the "Undecided" value, which is a valid value in other endpoints.
     * This is because a user cannot suggest to change the status of a student to "Undecided".
     */
    @PostMapping("/{id}/suggestions")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun addStudentStatusSuggestion(@PathVariable id: UUID, @RequestBody statusSuggestion: StatusSuggestion) =
        service.addStudentStatusSuggestion(id, statusSuggestion.status, statusSuggestion.motivation)
}