package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.EditionRepository
import be.osoc.team1.backend.services.EditionService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class EditionServiceTests {

    private val editionName = "edition"
    private val activeEdition = Edition(editionName, true)
    private val inactiveEdition = Edition(editionName, false)

    @Test
    fun `makeEditionInactive inactivates edition if it is active`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(activeEdition.name) } returns activeEdition
        every { repository.save(inactiveEdition) } returns inactiveEdition
        val service = EditionService(repository)
        service.makeEditionInactive(activeEdition.name)
        verify { repository.save(inactiveEdition) }
    }

    @Test
    fun `makeEditionInactive inactivates edition if it does not exist`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(activeEdition.name) } returns null
        every { repository.save(inactiveEdition) } returns inactiveEdition
        val service = EditionService(repository)
        service.makeEditionInactive(activeEdition.name)
        verify { repository.save(inactiveEdition) }
    }

    @Test
    fun `makeEditionInactive fails if edition is already inactive`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(inactiveEdition.name) } returns inactiveEdition
        val service = EditionService(repository)
        assertThrows<FailedOperationException> { service.makeEditionInactive(inactiveEdition.name) }
    }

    @Test
    fun `makeEditionActive activates edition if it is inactive and there is no other active edition`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(inactiveEdition.name) } returns inactiveEdition
        every { repository.findAll() } returns listOf(inactiveEdition)
        every { repository.save(activeEdition) } returns activeEdition
        val service = EditionService(repository)
        service.makeEditionActive(inactiveEdition.name)
        verify { repository.save(activeEdition) }
    }

    @Test
    fun `makeEditionActive activates edition if it does not exist and there is no other active edition`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(editionName) } returns null
        every { repository.findAll() } returns emptyList()
        every { repository.save(activeEdition) } returns activeEdition
        val service = EditionService(repository)
        service.makeEditionActive(editionName)
        verify { repository.save(activeEdition) }
    }

    @Test
    fun `makeEditionActive fails if the edition is already active`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(activeEdition.name) } returns activeEdition
        val service = EditionService(repository)
        assertThrows<FailedOperationException> { service.makeEditionActive(activeEdition.name) }
    }

    @Test
    fun `makeEditionActive fails if the edition is inactive but there is another active edition`() {
        val activeEditionWithDifferentName = Edition("another edition", true)
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(inactiveEdition.name) } returns inactiveEdition
        every { repository.findAll() } returns listOf(activeEditionWithDifferentName, inactiveEdition)
        val service = EditionService(repository)
        assertThrows<ForbiddenOperationException> { service.makeEditionActive(inactiveEdition.name) }
    }

    @Test
    fun `getActiveEdition returns the active edition if it exists`() {
        val repository = mockk<EditionRepository>()
        every { repository.findAll() } returns listOf(activeEdition)
        val service = EditionService(repository)
        assertEquals(activeEdition, service.getActiveEdition())
    }

    @Test
    fun `getActiveEdition returns null if there is no active edition`() {
        val repository = mockk<EditionRepository>()
        every { repository.findAll() } returns listOf(inactiveEdition)
        val service = EditionService(repository)
        assertEquals(null, service.getActiveEdition())
    }

    @Test
    fun `getInactiveEditions returns all inactive editions`() {
        val repository = mockk<EditionRepository>()
        every { repository.findAll() } returns listOf(activeEdition, inactiveEdition)
        val service = EditionService(repository)
        assertEquals(listOf(inactiveEdition), service.getInactiveEditions())
    }

    @Test
    fun `deleteEdition deletes edition if it exists`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(inactiveEdition.name) } returns true
        every { repository.deleteById(inactiveEdition.name) } just Runs
        val service = EditionService(repository)
        service.deleteEdition(inactiveEdition.name)
        verify { repository.deleteById(inactiveEdition.name) }
    }

    @Test
    fun `deleteEdition fails if edition does not exist`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(inactiveEdition.name) } returns false
        val service = EditionService(repository)
        assertThrows<InvalidIdException> { service.deleteEdition(inactiveEdition.name) }
    }
}
