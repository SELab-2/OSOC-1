package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.CommunicationTypeEnum
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.GenericEditionRepository
import be.osoc.team1.backend.services.CommunicationService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class CommunicationServiceTests {
    private val testId = UUID.randomUUID()
    private val testEdition = "testEdition"
    private val testCommunication = Communication("test message", CommunicationTypeEnum.Email)
    private val savedCommunication = Communication("a saved communication", CommunicationTypeEnum.Email)

    private fun getRepository(communicationAlreadyExists: Boolean): GenericEditionRepository<Communication> {
        val repository = mockk<GenericEditionRepository<Communication>>()
        every { repository.existsById(any()) } returns communicationAlreadyExists
        every { repository.findByIdAndEdition(testId, testEdition) } returns
            if (communicationAlreadyExists) testCommunication else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns savedCommunication
        return repository
    }

    @Test
    fun `getCommunicationById succeeds when communication with id exists`() {
        val service = CommunicationService(getRepository(true))
        Assertions.assertEquals(testCommunication, service.getCommunicationById(testId, testEdition))
    }

    @Test
    fun `getCommunicationById fails when no communication with that id exists`() {
        val service = CommunicationService(getRepository(false))
        assertThrows<InvalidIdException> { service.getCommunicationById(testId, testEdition) }
    }

    @Test
    fun `createCommunication saves communication`() {
        val repository = getRepository(false)
        val service = CommunicationService(repository)
        service.createCommunication(testCommunication)
        verify { repository.save(testCommunication) }
    }
}
