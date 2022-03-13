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
     * Get a student by their [id]. Throws an [InvalidStudentIdException] if no such student exists.
     */
    fun getStudentById(id: UUID) = repository.findByIdOrNull(id) ?: throw InvalidStudentIdException()

    /**
     * Delete a student by their [id]. Throws an [InvalidStudentIdException] if no such student existed
     * in the database in the first place.
     */
    fun deleteStudentById(id: UUID) {
        if (!repository.existsById(id))
            throw InvalidStudentIdException()

        repository.deleteById(id)
    }

    /**
     * Add the given [student] entity to the database. Returns the student's new id as decided by the database.
     */
    fun addStudent(student: Student) = repository.save(student).id

    /**
     * Retrieve the student with the specified [id], then set his status to [newStatus].
     * Throws an [InvalidStudentIdException] if no student with that [id] exists.
     */
    fun setStudentStatus(id: UUID, newStatus: StatusEnum) {
        val student = getStudentById(id)
        student.status = newStatus
        repository.save(student)
    }

    /**
     * Retrieve the [Student] with the specified [id], then create a new [StatusSuggestion] based
     * on the information in the given [statusSuggestion] and add it to the [Student]'s list.
     * Throws an [InvalidStudentIdException] if no student with that [id] exists.
     */
    fun addStudentStatusSuggestion(id: UUID, statusSuggestion: StatusSuggestion) {
        val student = getStudentById(id)
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
        // TODO: check if coach actually exists (need User endpoint for this)
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
