package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.StudentRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class StudentService(private val repository: StudentRepository) {

    fun getAllStudents(): Iterable<Student> = repository.findAll()

    /**
     * Get a student by their [id]. Throws an InvalidIdException if no such student exists.
     */
    fun getStudentById(id: UUID): Student = repository.findByIdOrNull(id) ?: throw InvalidIdException()

    /**
     * Delete a student by their [id]. Throws an InvalidIdException if no such student existed
     * in the database in the first place.
     */
    fun deleteStudentById(id: UUID) {
        if (repository.existsById(id))
            repository.deleteById(id)
        else
            throw InvalidIdException()
    }

    /**
     * Add the given [student] entity to the database. Throws an IllegalArgumentException
     * if a student already exists with the same id.
     */
    fun putStudent(student: Student) {
        if (repository.existsById(student.id))
            throw IllegalArgumentException()
        else
            repository.save(student)
    }

    /**
     * Change the values of a student already present in the database. Throws an
     * InvalidIdException if no student with that id exists.
     */
    fun patchStudent(student: Student) {
        if (repository.existsById(student.id))
            repository.save(student)
        else
            throw InvalidIdException()
    }
}
