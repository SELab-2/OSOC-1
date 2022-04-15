package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.ActiveEdition
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.repositories.EditionRepository
import be.osoc.team1.backend.services.EditionService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EditionServiceTests {

    private val testEdition = ActiveEdition("edition")

    @Test
    fun `makeEditionInactive inactivates edition if it is active`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(testEdition.name) } returns true
        every { repository.delete(testEdition) } just Runs
        val service = EditionService(repository)
        service.makeEditionInactive(testEdition)
        verify { repository.delete(testEdition) }
    }

    @Test
    fun `makeEditionInactive fails if edition is already inactive`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(testEdition.name) } returns false
        val service = EditionService(repository)
        assertThrows<FailedOperationException> { service.makeEditionInactive(testEdition) }
    }

    @Test
    fun `makeEditionActive activates edition if it is inactive and there is no other active edition`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(testEdition.name) } returns false
        every { repository.findAll() } returns emptyList()
        every { repository.save(testEdition) } returns testEdition
        val service = EditionService(repository)
        service.makeEditionActive(testEdition)
        verify { repository.save(testEdition) }
    }

    @Test
    fun `makeEditionActive fails if edition is already active`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(testEdition.name) } returns true
        val service = EditionService(repository)
        assertThrows<FailedOperationException> { service.makeEditionActive(testEdition) }
    }

    @Test
    fun `makeEditionActive fails if there is another active edition`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(testEdition.name) } returns false
        every { repository.findAll() } returns listOf(ActiveEdition("Another active edition"))
        val service = EditionService(repository)
        assertThrows<ForbiddenOperationException> { service.makeEditionActive(testEdition) }
    }
}