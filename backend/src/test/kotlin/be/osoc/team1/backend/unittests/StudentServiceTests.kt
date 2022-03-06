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
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.springframework.data.repository.findByIdOrNull

class StudentServiceTests {

    private val testStudent = Student("Tom", "Alard")
    private val testId = testStudent.id

    private fun getRepository(studentAlreadyExists: Boolean): StudentRepository {
        val repository: StudentRepository = mockk()
        every { repository.existsById(testId) } returns studentAlreadyExists
        every { repository.findByIdOrNull(testId) } returns if (studentAlreadyExists) testStudent else null
        every { repository.deleteById(testId) } just Runs
        val differentIdTestStudent = Student("Tom", "Alard")
        every { repository.save(testStudent) } returns differentIdTestStudent
        return repository
    }

    @Test
    fun `getStudentById succeeds when student with id exists`() {
        val service = StudentService(getRepository(true))
        assertEquals(service.getStudentById(testId), testStudent)
    }

    @Test(expected = InvalidIdException::class)
    fun `getStudentById fails when no student with that id exists`() {
        val service = StudentService(getRepository(false))
        service.getStudentById(testId)
    }

    @Test
    fun `deleteStudentById succeeds when student with id exists`() {
        val repository = getRepository(true)
        val service = StudentService(repository)
        service.deleteStudentById(testId)
        verify { repository.deleteById(testId) }
    }

    @Test(expected = InvalidIdException::class)
    fun `deleteStudentById fails when no student with that id exists`() {
        val service = StudentService(getRepository(false))
        service.deleteStudentById(testId)
    }

    @Test
    fun `putStudent saves student`() {
        val repository = getRepository(false)
        val service = StudentService(repository)
        service.putStudent(testStudent)
        verify { repository.save(testStudent) }
    }

    @Test
    fun `putStudent returns some other id than what was passed`() {
        val service = StudentService(getRepository(false))
        assertNotEquals(service.putStudent(testStudent), testId)
    }
}
