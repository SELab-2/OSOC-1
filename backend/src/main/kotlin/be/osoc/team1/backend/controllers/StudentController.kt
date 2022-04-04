package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.BadFilterException
import be.osoc.team1.backend.services.StudentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/students")
class StudentController(private val service: StudentService) {

    /**
     * Get a list of all students in the database. This request cannot fail.
     * There are default values applied for paging ([pageNumber], [pageSize] and [sortBy]),
     * these can be modified by adding request parameters to the url.
     *
     * Can also filter the results by [name] (default value is empty so no student is excluded),
     * by [status] (default value allows all statuses)
     * by [includeSuggested] (default value is true, so you will also see students you already suggested for)
     */
    @GetMapping
    @Secured("ROLE_COACH")
    fun getAllStudents(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "50") pageSize: Int,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "Yes,No,Maybe,Undecided") status: List<StatusEnum>,
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(defaultValue = "true") includeSuggested: Boolean,
    ): Iterable<Student> = service.getAllStudents(pageNumber, pageSize, sortBy, status, name, includeSuggested)

    /**
     * Returns the student with the corresponding [studentId]. If no such student exists,
     * returns a "404: Not Found" message instead.
     */
    @GetMapping("/{studentId}")
    @Secured("ROLE_COACH")
    fun getStudentById(@PathVariable studentId: UUID): Student = service.getStudentById(studentId)

    /**
     * Deletes the student with the corresponding [studentId]. If no such student exists,
     * returns a "404: Not Found" message instead.
     */
    @DeleteMapping("/{studentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun deleteStudentById(@PathVariable studentId: UUID) = service.deleteStudentById(studentId)

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
    @Secured("ROLE_COACH")
    fun addStudent(@RequestBody student: Student): ResponseEntity<Student> {
        val createdStudent = service.addStudent(student)
        return getObjectCreatedResponse(createdStudent.id, createdStudent)
    }

    /**
     * Set the [status] of the student with the given [studentId]. If no such student exists,
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
    @PostMapping("/{studentId}/status")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun setStudentStatus(@PathVariable studentId: UUID, @RequestBody status: StatusEnum) =
        service.setStudentStatus(studentId, status)

    /**
     * Add a [statusSuggestion] to the student with the given [studentId]. The coachId field should be equal to the id
     * of the coach who is making this suggestion. If either of these id's do not have a matching record
     * in the database, a "404: Not Found" message is returned to the caller instead. The [statusSuggestion] should be
     * passed in the request body as a JSON object and should have the following format:
     *
     * ```
     * {
     *      "coachId": "(INSERT ID)"
     *      "status": "Yes" OR "Maybe" OR "No",
     *      "motivation": "(INSERT MOTIVATION)"
     * }
     *```
     *
     * Any other values for the status will result in a "400: Bad Request" response.
     * Importantly, this includes the "Undecided" value, which is a valid value in other endpoints.
     * This is because a user cannot suggest to change the status of a student to "Undecided".
     */
    @PostMapping("/{studentId}/suggestions")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_COACH")
    fun addStudentStatusSuggestion(@PathVariable studentId: UUID, @RequestBody statusSuggestion: StatusSuggestion) =
        service.addStudentStatusSuggestion(studentId, statusSuggestion)

    /**
     * Deletes the [StatusSuggestion] made by the coach identified by the given [coachId]
     * from the [Student] with the given [studentId]. If the student doesn't exist, a
     * "404: Not Found" message is returned instead. Additionally, if the student does exist, but
     * the coach hasn't made a suggestion for this student, a "400: Bad Request" message will be returned.
     */
    @DeleteMapping("/{studentId}/suggestions/{coachId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_COACH")
    fun deleteStudentStatusSuggestion(@PathVariable studentId: UUID, @PathVariable coachId: UUID) =
        service.deleteStudentStatusSuggestion(studentId, coachId)
}
