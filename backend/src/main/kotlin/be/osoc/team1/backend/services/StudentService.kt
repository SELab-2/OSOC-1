package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidStudentIdException
import be.osoc.team1.backend.exceptions.InvalidUserIdException
import be.osoc.team1.backend.repositories.StudentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StudentService(private val repository: StudentRepository, private val userService: UserService) {

    /**
     * Get all students within paging range ([pageNumber], [pageSize]) and sorted [sortBy].
     * Can be filtered by [name] (requested string gets processed to more easily give matches),
     * [statusFilter] (see if student status matches 1 in the given list),
     * whether or not the requesting user has already made a suggestion for this student [includeSuggested],
     * [callee] is the user who made this request
     */
    fun getAllStudents(
        pageNumber: Int,
        pageSize: Int,
        sortBy: String,
        statusFilter: List<StatusEnum>,
        name: String,
        includeSuggested: Boolean,
        callee: User
    ): Iterable<Student> {
        val paging: Pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy))
        val pagedResult: Page<Student> = repository.findAll(paging)
        val studentList = mutableListOf<Student>()
        for (student in pagedResult.content) {
            val studentHasStatus = statusFilter.contains(student.status)
            // concat first- and lastname make lowercase and remove spaces and see if that matches the input (which is formatted exactly the same)
            val studentHasMatchingName = (student.firstName + student.lastName).lowercase().replace(" ", "")
                .contains(name.lowercase().replace(" ", ""))
            val studentBeenSuggestedByUser = student.statusSuggestions.any { it.coachId == callee.id }
            if (studentHasStatus && studentHasMatchingName && (includeSuggested || !studentBeenSuggestedByUser)) {
                studentList.add(student)
            }
        }
        return studentList
    }

    /**
     * Get a student by their [studentId]. Throws an [InvalidStudentIdException] if no such student exists.
     */
    fun getStudentById(studentId: UUID) = repository.findByIdOrNull(studentId) ?: throw InvalidStudentIdException()

    /**
     * Delete a student by their [studentId]. Throws an [InvalidStudentIdException] if no such student existed
     * in the database in the first place.
     */
    fun deleteStudentById(studentId: UUID) {
        if (!repository.existsById(studentId))
            throw InvalidStudentIdException()

        repository.deleteById(studentId)
    }

    /**
     * Add the given [student] entity to the database. Returns the created student.
     */
    fun addStudent(student: Student): Student = repository.save(student)

    /**
     * Retrieve the student with the specified [studentId], then set his status to [newStatus].
     * Throws an [InvalidStudentIdException] if no student with that [studentId] exists.
     */
    fun setStudentStatus(studentId: UUID, newStatus: StatusEnum) {
        val student = getStudentById(studentId)
        student.status = newStatus
        repository.save(student)
    }

    /**
     * Retrieve the [Student] with the specified [studentId], then create a new [StatusSuggestion] based
     * on the information in the given [statusSuggestion] and add it to the [Student]'s list.
     * A coach is only allowed to make one [StatusSuggestion] for one particular [Student].
     * If the coach making this [statusSuggestion] has already made one for this [Student],
     * the method will throw a [ForbiddenOperationException]. If a coach wants to change their suggestion,
     * the API caller should first delete the original suggestion with the [deleteStudentStatusSuggestion] method,
     * and then call this method. Additionally throws an [InvalidStudentIdException]
     * if no student with that [studentId] exists, an [InvalidUserIdException] if no [User] with
     * the given coachId exists, and a [ForbiddenOperationException] if the [User] exists but doesn't have
     * the coach role.
     */
    fun addStudentStatusSuggestion(studentId: UUID, statusSuggestion: StatusSuggestion) {
        val coach = userService.getUserById(statusSuggestion.coachId)
        if (!coach.role.hasPermissionLevel(Role.Coach)) {
            throw ForbiddenOperationException("Only coaches and admins can make status suggestions.")
        }
        val student = getStudentById(studentId)
        val sameCoachSuggestion = student.statusSuggestions.find { it.coachId == coach.id }
        if (sameCoachSuggestion !== null) {
            throw ForbiddenOperationException("This coach has already made a suggestion for this student.")
        }
        student.statusSuggestions.add(statusSuggestion)
        // See the comment at StatusSuggestion.student to understand why we have to do this.
        statusSuggestion.student = student
        repository.save(student)
    }

    /**
     * Retrieve the [Student] with the specified [studentId], then get the [StatusSuggestion]
     * that was made by the coach identified by the given [coachId] and delete it.
     * Throws an [InvalidStudentIdException] if no [Student] with that [studentId] exists,
     * an [InvalidUserIdException] if no [User] with that [coachId] exists,
     * or a [FailedOperationException] if the student and the coach exist, but the coach
     * hasn't made a [StatusSuggestion] for this student.
     */
    fun deleteStudentStatusSuggestion(studentId: UUID, coachId: UUID) {
        val coach = userService.getUserById(coachId)
        val student = getStudentById(studentId)
        val suggestion = student.statusSuggestions.find { it.coachId == coach.id }
        if (suggestion === null) {
            throw FailedOperationException("This coach hasn't made a suggestion for the given student.")
        }
        student.statusSuggestions.remove(suggestion)
        // See the comment at StatusSuggestion.student to understand why we have to do this.
        suggestion.student = null
        repository.save(student)
    }

    /**
     * Adds a communication to student based on [studentId], if [studentId] is not in [repository] throw [InvalidStudentIdException]
     */
    fun addCommunicationToStudent(studentId: UUID, communication: Communication) {
        val student = getStudentById(studentId)
        student.communications.add(communication)
        repository.save(student)
    }
}
