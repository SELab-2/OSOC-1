package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.filterByAlumn
import be.osoc.team1.backend.entities.filterByName
import be.osoc.team1.backend.entities.filterByNotYetAssigned
import be.osoc.team1.backend.entities.filterBySkills
import be.osoc.team1.backend.entities.filterByStatus
import be.osoc.team1.backend.entities.filterByStudentCoach
import be.osoc.team1.backend.entities.filterBySuggested
import be.osoc.team1.backend.exceptions.UnauthorizedOperationException
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.services.OsocUserDetailService
import be.osoc.team1.backend.services.PagedCollection
import be.osoc.team1.backend.services.Pager
import be.osoc.team1.backend.services.StudentService
import be.osoc.team1.backend.services.applyIf
import be.osoc.team1.backend.services.page
import be.osoc.team1.backend.util.TallyDeserializer
import org.springframework.data.domain.Sort
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
import java.net.URLDecoder
import java.security.Principal
import java.util.UUID

@RestController
@RequestMapping("/{edition}/students")
class StudentController(
    private val service: StudentService,
    private val userDetailService: OsocUserDetailService,
    private val assignmentRepository: AssignmentRepository
) {

    /**
     * Get a list of all students in the database who are a part of the given OSOC [edition].
     * This request cannot fail. There are default values applied for paging ([pageNumber], [pageSize] and [sortBy]).
     * These can be modified by adding request parameters to the url.
     *
     * The results can also be filtered by [name] (default value is empty so no student is excluded),
     * by [status] (default value allows all statuses), by [includeSuggested] (default value is true, so
     * you will also see students you already suggested for), by [skills], by only alumni students([alumnOnly]), by only student coach
     * volunteers([onlyStudentCoach]) and by only unassigned students ([onlyNotAssigned]) students.
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
        @RequestParam(defaultValue = "") skills: Set<String>,
        @RequestParam(defaultValue = "false") alumnOnly: Boolean,
        @RequestParam(defaultValue = "false") onlyStudentCoach: Boolean,
        @RequestParam(defaultValue = "false") onlyNotAssigned: Boolean,
        @PathVariable edition: String,
        principal: Principal
    ): PagedCollection<Student> {
        val decodedName = URLDecoder.decode(name, "UTF-8")
        val callee = userDetailService.getUserFromPrincipal(principal)
        val pager = Pager(pageNumber, pageSize)
        return service.getAllStudents(Sort.by(sortBy), edition)
            .filterByName(decodedName)
            .filterBySuggested(includeSuggested, callee)
            .filterByStatus(status)
            .applyIf(skills.isNotEmpty()) { filterBySkills(skills) }
            .applyIf(alumnOnly) { filterByAlumn() }
            .applyIf(onlyStudentCoach) { filterByStudentCoach() }
            .applyIf(onlyNotAssigned) { filterByNotYetAssigned(assignmentRepository) }
            .page(pager)
    }

    /**
     * Returns the student with the corresponding [studentId]. If no such student exists, returns a
     * "404: Not Found" message instead.
     */
    @GetMapping("/{studentId}")
    @Secured("ROLE_COACH")
    fun getStudentById(@PathVariable studentId: UUID, @PathVariable edition: String): Student =
        service.getStudentById(studentId, edition)

    /**
     * Deletes the student with the corresponding [studentId]. If no such student exists, returns a
     * "404: Not Found" message instead.
     */
    @DeleteMapping("/{studentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun deleteStudentById(@PathVariable studentId: UUID, @PathVariable edition: String) =
        service.deleteStudentById(studentId)

    /**
     * Add a student to the database. The student should be passed in the request body as a JSON representation of a
     * tally form submission. The form is expected to contain certain specific questions, these are specified in the
     * [TallyDeserializer] class.
     *
     * The location of the newly created student is then returned to the API caller in the location
     * header. No checking is done to see if firstName or lastName qualify as valid 'names'. This
     * verification is the responsibility of the caller.
     */
    @PostMapping
    fun addStudent(
        @RequestBody studentRegistration: Student,
        @PathVariable edition: String
    ): ResponseEntity<Student> {
        studentRegistration.answers.forEach { it.edition = edition }
        val student = Student(
            studentRegistration.firstName,
            studentRegistration.lastName,
            edition,
            studentRegistration.skills,
            studentRegistration.alumn,
            studentRegistration.possibleStudentCoach,
            studentRegistration.answers
        )
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
     * Any other input value will result in a "400: Bad Request" response. These values are also
     * case-sensitive.
     */
    @PostMapping("/{studentId}/status")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun setStudentStatus(@PathVariable studentId: UUID, @RequestBody status: StatusEnum, @PathVariable edition: String) =
        service.setStudentStatus(studentId, status, edition)

    /**
     * Add a [statusSuggestion] to the student with the given [studentId]. The coachId field should
     * be equal to the id of the coach who is making this suggestion, so equal to the id of the
     * currently authenticated user. If either of these id's do not have a matching record in the
     * database, a "404: Not Found" message is returned to the caller instead. If the coachId does
     * not match the id of the currently authenticated user a '401: Unauthorized" is returned. The
     * [statusSuggestion] should be passed in the request body as a JSON object and should have the
     * following format:
     *
     * ```
     * {
     *      "coachId": "(INSERT ID)"
     *      "status": "Yes" OR "Maybe" OR "No",
     *      "motivation": "(INSERT MOTIVATION)"
     * }
     * ```
     *
     * Any other values for the status will result in a "400: Bad Request" response. Importantly,
     * this includes the "Undecided" value, which is a valid value in other endpoints. This is
     * because a user cannot suggest changing the status of a student to "Undecided".
     */
    @PostMapping("/{studentId}/suggestions")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_COACH")
    fun addStudentStatusSuggestion(
        @PathVariable studentId: UUID,
        @RequestBody statusSuggestion: StatusSuggestion,
        @PathVariable edition: String,
        principal: Principal,
    ) {
        val user = userDetailService.getUserFromPrincipal(principal)
        if (statusSuggestion.coachId != user.id)
            throw UnauthorizedOperationException(
                "The 'coachId' did not equal authenticated user id!"
            )

        service.addStudentStatusSuggestion(studentId, statusSuggestion, edition)
    }

    /**
     * Deletes the [StatusSuggestion] made by the coach identified by the given [coachId] from the
     * [Student] with the given [studentId]. If the student doesn't exist, a "404: Not Found"
     * message is returned instead. Additionally, if the student does exist, but the coach hasn't
     * made a suggestion for this student, a "400: Bad Request" message will be returned. If the
     * user attempts to remove a [StatusSuggestion] that was not made by them a "401: Unauthorized"
     * is returned.
     */
    @DeleteMapping("/{studentId}/suggestions/{coachId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_COACH")
    fun deleteStudentStatusSuggestion(
        @PathVariable studentId: UUID,
        @PathVariable coachId: UUID,
        @PathVariable edition: String,
        principal: Principal
    ) {
        val user = userDetailService.getUserFromPrincipal(principal)
        if (coachId != user.id)
            throw UnauthorizedOperationException(
                "The 'coachId' did not equal authenticated user id. You can't remove suggestions from other users!"
            )

        service.deleteStudentStatusSuggestion(studentId, coachId, edition)
    }
}
