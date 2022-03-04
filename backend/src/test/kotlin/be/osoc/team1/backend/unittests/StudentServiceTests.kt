package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.services.StudentService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.Test
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class StudentServiceTests {

    private val testId: UUID = UUID.randomUUID()
    private val testStudent = Student(testId, "Tom", "Alard")

    private fun getService(studentAlreadyExists: Boolean): StudentService {
        val repository: StudentRepository = mockk()
        every { repository.existsById(testId) } returns studentAlreadyExists
        every { repository.findByIdOrNull(testId) } returns if (studentAlreadyExists) testStudent else null
        every { repository.deleteById(testId) } just Runs
        every { repository.save(testStudent) } returns testStudent
        return StudentService(repository)
    }

    @Test
    fun `getStudentById succeeds when student with id exists`() {
        val service = getService(true)
        service.getStudentById(testId)
    }

    @Test(expected = InvalidIdException::class)
    fun `getStudentById fails when no student with that id exists`() {
        val service = getService(false)
        service.getStudentById(testId)
    }

    @Test
    fun `deleteStudentById succeeds when student with id exists`() {
        val service = getService(true)
        service.deleteStudentById(testId)
    }

    @Test(expected = InvalidIdException::class)
    fun `deleteStudentById fails when no student with that id exists`() {
        val service = getService(false)
        service.deleteStudentById(testId)
    }

    @Test
    fun `putStudent puts student in database when no other student with same id exists`() {
        val service = getService(false)
        service.putStudent(testStudent)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `putStudent fails when student with same id already exists`() {
        val service = getService(true)
        service.putStudent(testStudent)
    }

    @Test
    fun `patchStudent updates student when student with same id exists`() {
        val service = getService(true)
        service.patchStudent(testStudent)
    }

    @Test(expected = InvalidIdException::class)
    fun `patchStudent fails when no student with same id exists`() {
        val service = getService(false)
        service.patchStudent(testStudent)
    }
}