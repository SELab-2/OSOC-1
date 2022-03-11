package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.TypeEnum
import be.osoc.team1.backend.repositories.CommunicationRepository
import be.osoc.team1.backend.services.CommunicationService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class CommunicationServiceTests {
    private val testId = UUID.randomUUID()
    private val testStudent = Student("Jitse", "Willaert")
    private val testCommunication = Communication("test message", TypeEnum.Email, testStudent)
    private val savedCommunication = Communication("a saved communication", TypeEnum.Email, testStudent)

    private fun getRepository(communicationAlreadyExists: Boolean): CommunicationRepository {
        val repository: CommunicationRepository = mockk()
        every { repository.existsById(any()) } returns communicationAlreadyExists
        every { repository.findByIdOrNull(any()) } returns if (communicationAlreadyExists) testCommunication else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns savedCommunication
        return repository
    }

    @Test
    fun `postCommunication saves communication`() {
        val repository = getRepository(false)
        val service = CommunicationService(repository)
        service.postCommunication(testCommunication)
        verify { repository.save(testCommunication) }
    }

    @Test
    fun `postCommunication returns some other id than what was passed`() {
        val service = CommunicationService(getRepository(false))
        Assertions.assertNotEquals(service.postCommunication(testCommunication), testId)
    }
}
