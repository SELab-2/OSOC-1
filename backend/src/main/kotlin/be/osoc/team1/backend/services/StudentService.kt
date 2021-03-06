package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidCommunicationIdException
import be.osoc.team1.backend.exceptions.InvalidStudentIdException
import be.osoc.team1.backend.exceptions.InvalidUserIdException
import be.osoc.team1.backend.repositories.CommunicationRepository
import be.osoc.team1.backend.repositories.StudentRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StudentService(
    private val repository: StudentRepository,
    private val userService: UserService,
    private val communicationRepository: CommunicationRepository
) {

    /**
     * Get all students sorted using [sortBy].
     * Can be filtered using the List<Student> extension functions.
     */
    fun getAllStudents(sortBy: Sort, edition: String): List<Student> =
        repository.findByEdition(edition, sortBy).toList()

    /**
     * Get a student by their [studentId]. Throws an [InvalidStudentIdException] if no such student exists.
     */
    fun getStudentById(studentId: UUID, edition: String) =
        repository.findByIdAndEdition(studentId, edition) ?: throw InvalidStudentIdException()

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
    fun setStudentStatus(studentId: UUID, newStatus: StatusEnum, edition: String) {
        val student = getStudentById(studentId, edition)
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
    fun addStudentStatusSuggestion(studentId: UUID, statusSuggestion: StatusSuggestion, edition: String) {
        val coach = statusSuggestion.suggester
        val student = getStudentById(studentId, edition)
        val sameCoachSuggestion = student.statusSuggestions.find { it.suggester == coach }
        if (sameCoachSuggestion !== null) {
            throw ForbiddenOperationException("This coach has already made a suggestion for this student.")
        }
        student.statusSuggestions.add(statusSuggestion)
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
    fun deleteStudentStatusSuggestion(studentId: UUID, coachId: UUID, edition: String) {
        val coach = userService.getUserById(coachId)
        val student = getStudentById(studentId, edition)
        val suggestion = student.statusSuggestions.find { it.suggester == coach }
        if (suggestion === null) {
            throw FailedOperationException("This coach hasn't made a suggestion for the given student.")
        }
        student.statusSuggestions.remove(suggestion)
        repository.save(student)
    }

    /**
     * Adds a communication to the student that is referenced in the [communication] student field
     */
    fun addCommunicationToStudent(communication: Communication, edition: String) {
        val student = communication.student
        student.communications.add(communication)
        repository.save(student)
    }

    /**
     * Removes a communication from the student that is referenced in the communication with [communicationId]
     */
    fun removeCommunicationFromStudent(communicationId: UUID, edition: String) {
        val communication = communicationRepository.findByIdAndEdition(communicationId, edition)
            ?: throw InvalidCommunicationIdException()
        val student = communication.student
        student.communications.remove(communication)
        repository.save(student)
    }
}
