package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.SuggestionEnum
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.StudentRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StudentService(private val repository: StudentRepository) {

    fun getAllStudents(): Iterable<Student> = repository.findAll()

    /**
     * Get a student by their [id]. Throws an InvalidIdException if no such student exists.
     */
    fun getStudentById(id: UUID) = repository.findByIdOrNull(id) ?: throw InvalidIdException()

    /**
     * Delete a student by their [id]. Throws an InvalidIdException if no such student existed
     * in the database in the first place.
     */
    fun deleteStudentById(id: UUID) {
        if (!repository.existsById(id))
            throw InvalidIdException()

        repository.deleteById(id)
    }

    /**
     * Add the given [student] entity to the database. Returns the student's new id as decided by the database.
     */
    fun addStudent(student: Student) = repository.save(student).id

    /**
     * Retrieve the student with the specified [id], then set his status to [newStatus].
     * Throws an InvalidIdException if no student with that [id] exists.
     */
    fun setStudentStatus(id: UUID, newStatus: StatusEnum) {
        val student = getStudentById(id)
        student.status = newStatus
        repository.save(student)
    }

    /**
     * Retrieve the student with the specified [id], then create a new StatusSuggestion with
     * the given [suggestionEnum] and [motivation] and add it to the student's list.
     * Throws an InvalidIdException if no student with that [id] exists.
     */
    fun addStudentStatusSuggestion(id: UUID, suggestionEnum: SuggestionEnum, motivation: String) {
        val student = getStudentById(id)
        val suggestion = StatusSuggestion(suggestionEnum, motivation)
        student.statusSuggestions.add(suggestion)
        repository.save(student)
    }
}
