package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.InvalidCoachIdException
import be.osoc.team1.backend.exceptions.InvalidStudentIdException
import be.osoc.team1.backend.repositories.StudentRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StudentService(private val repository: StudentRepository) {

    fun getAllStudents(): Iterable<Student> = repository.findAll()

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
     * Add the given [student] entity to the database. Returns the student's new id as decided by the database.
     */
    fun addStudent(student: Student) = repository.save(student).id

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
     * the method will throw a [FailedOperationException]. If a coach wants to change their suggestion,
     * the API caller should first delete the original suggestion with the [deleteStudentStatusSuggestion] method,
     * and then call this method. Throws an [InvalidStudentIdException] if no student with that [studentId] exists.
     */
    fun addStudentStatusSuggestion(studentId: UUID, statusSuggestion: StatusSuggestion) {
        val student = getStudentById(studentId)
        val sameCoachSuggestion = student.statusSuggestions.find { it.coachId == statusSuggestion.coachId }
        if (sameCoachSuggestion !== null) {
            throw FailedOperationException("This coach has already made a suggestion for this student.")
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
     * or an [InvalidCoachIdException] if either no coach exists with that [coachId],
     * or if the coach does exist, but he hasn't made a [StatusSuggestion] for this [Student].
     */
    fun deleteStudentStatusSuggestion(studentId: UUID, coachId: UUID) {
        val student = getStudentById(studentId)
        val suggestion = student.statusSuggestions.find { it.coachId == coachId } ?: throw InvalidCoachIdException()
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
