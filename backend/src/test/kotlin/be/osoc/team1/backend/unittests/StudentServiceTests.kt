package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.SuggestionEnum
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.services.StudentService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class StudentServiceTests {

    private val testStudent = Student("Tom", "Alard")
    private val testId = testStudent.id
    private val testMotivation = "test motivation"

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

    @Test
    fun `getStudentById fails when no student with that id exists`() {
        val service = StudentService(getRepository(false))
        assertThrows<InvalidIdException> { service.getStudentById(testId) }
    }

    @Test
    fun `deleteStudentById succeeds when student with id exists`() {
        val repository = getRepository(true)
        val service = StudentService(repository)
        service.deleteStudentById(testId)
        verify { repository.deleteById(testId) }
    }

    @Test
    fun `deleteStudentById fails when no student with that id exists`() {
        val service = StudentService(getRepository(false))
        assertThrows<InvalidIdException> { service.deleteStudentById(testId) }
    }

    @Test
    fun `addStudent saves student`() {
        val repository = getRepository(false)
        val service = StudentService(repository)
        service.addStudent(testStudent)
        verify { repository.save(testStudent) }
    }

    @Test
    fun `addStudent returns some other id than what was passed`() {
        val service = StudentService(getRepository(false))
        assertNotEquals(service.addStudent(testStudent), testId)
    }

    @Test
    fun `setStudentStatus changes student status when student with id exists`() {
        val repository = getRepository(true)
        val service = StudentService(repository)
        service.setStudentStatus(testId, StatusEnum.Yes)
        testStudent.status = StatusEnum.Yes // Bit of a hack
        verify { repository.save(testStudent) }
        testStudent.status = StatusEnum.Undecided
    }

    @Test
    fun `setStudentStatus fails when no student with that id exists`() {
        val service = StudentService(getRepository(false))
        assertThrows<InvalidIdException> { service.setStudentStatus(testId, StatusEnum.Yes) }
    }

    @Test
    fun `addStudentStatusSuggestion adds status suggestion to list when student with id exists`() {
        val repository = getRepository(true)
        val service = StudentService(repository)
        val testSuggestion = StatusSuggestion(SuggestionEnum.Yes, testMotivation)
        service.addStudentStatusSuggestion(testId, testSuggestion.status, testSuggestion.motivation)
        testStudent.statusSuggestions.add(testSuggestion) // Bit of a hack
        verify { repository.save(testStudent) }
        testStudent.statusSuggestions.remove(testSuggestion)
    }

    @Test
    fun `addStudentStatusSuggestion fails when no student with that id exists`() {
        val service = StudentService(getRepository(false))
        assertThrows<InvalidIdException> { service.addStudentStatusSuggestion(testId, SuggestionEnum.Yes, "") }
    }
}
