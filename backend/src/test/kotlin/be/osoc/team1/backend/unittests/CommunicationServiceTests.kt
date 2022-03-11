package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.TypeEnum
import be.osoc.team1.backend.repositories.CommunicationRepository
import be.osoc.team1.backend.services.CommunicationService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class CommunicationServiceTests {
    private val testCommunication = Communication("test message", TypeEnum.Email)
    private val savedCommunication = Communication("a saved communication", TypeEnum.Email)

    private fun getRepository(communicationAlreadyExists: Boolean): CommunicationRepository {
        val repository: CommunicationRepository = mockk()
        every { repository.existsById(any()) } returns communicationAlreadyExists
        every { repository.findByIdOrNull(any()) } returns if (communicationAlreadyExists) testCommunication else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns savedCommunication
        return repository
    }

    @Test
    fun `createCommunication saves communication`() {
        val repository = getRepository(false)
        val service = CommunicationService(repository)
        service.createCommunication(testCommunication)
        verify { repository.save(testCommunication) }
    }
}
