package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.services.AssignmentService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class AssignmentServiceTests {
    private val testId = UUID.randomUUID()
    private val testStudent = Student("Jitse", "Willaert", "testEdition")
    private val testSkill = Skill("Test")
    private val testPosition = Position(testSkill, 2)
    private val testSuggester = User("Jitse", "Willaert", Role.Admin, "Test")
    private val testAssignment = Assignment(testStudent, testPosition, testSuggester, "reason")

    private fun getRepository(assignmentAlreadyExists: Boolean): AssignmentRepository {
        val repository: AssignmentRepository = mockk()
        every { repository.existsById(any()) } returns assignmentAlreadyExists
        every { repository.findByIdOrNull(any()) } returns if (assignmentAlreadyExists) testAssignment else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns testAssignment
        return repository
    }

    @Test
    fun `getAssignmentById succeeds when assignment with id exists`() {
        val service = AssignmentService(getRepository(true))
        Assertions.assertEquals(testAssignment, service.getById(testId))
    }

    @Test
    fun `getAssignmentById fails when no assignment with that id exists`() {
        val service = AssignmentService(getRepository(false))
        assertThrows<InvalidIdException> { service.getById(testId) }
    }
}
