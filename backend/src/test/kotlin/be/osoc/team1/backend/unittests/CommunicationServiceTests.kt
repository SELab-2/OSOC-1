package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.CommunicationTypeEnum
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.CommunicationRepository
import be.osoc.team1.backend.services.CommunicationService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class CommunicationServiceTests {
    private val testStudent = Student("firstname", "lastname")
    private val testId = testStudent.id
    private val testCommunication = Communication("test message", CommunicationTypeEnum.Email, "", testStudent)
    private val savedCommunication = Communication("a saved communication", CommunicationTypeEnum.Email, "", testStudent)

    private fun getRepository(communicationAlreadyExists: Boolean): CommunicationRepository {
        val repository: CommunicationRepository = mockk()
        every { repository.existsById(any()) } returns communicationAlreadyExists
        every { repository.findByIdOrNull(any()) } returns if (communicationAlreadyExists) testCommunication else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns savedCommunication
        return repository
    }

    @Test
    fun `getCommunicationById succeeds when communication with id exists`() {
        val service = CommunicationService(getRepository(true))
        Assertions.assertEquals(testCommunication, service.getById(testId))
    }

    @Test
    fun `getCommunicationById fails when no communication with that id exists`() {
        val service = CommunicationService(getRepository(false))
        assertThrows<InvalidIdException> { service.getById(testId) }
    }
}
