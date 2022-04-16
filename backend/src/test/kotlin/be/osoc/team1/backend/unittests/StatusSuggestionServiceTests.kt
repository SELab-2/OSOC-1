package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.SuggestionEnum
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.StatusSuggestionRepository
import be.osoc.team1.backend.services.StatusSuggestionService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class StatusSuggestionServiceTests {
    private val testId = UUID.randomUUID()
    private val coachId = UUID.randomUUID()
    private val testStatusSuggestion = StatusSuggestion(coachId, SuggestionEnum.Yes, "motivation")

    private fun getRepository(statusSuggestionAlreadyExists: Boolean): StatusSuggestionRepository {
        val repository: StatusSuggestionRepository = mockk()
        every { repository.existsById(any()) } returns statusSuggestionAlreadyExists
        every { repository.findByIdOrNull(any()) } returns if (statusSuggestionAlreadyExists) testStatusSuggestion else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns testStatusSuggestion
        return repository
    }

    @Test
    fun `getPositionById succeeds when position with id exists`() {
        val service = StatusSuggestionService(getRepository(true))
        Assertions.assertEquals(testStatusSuggestion, service.getById(testId))
    }

    @Test
    fun `getPositionById fails when no position with that id exists`() {
        val service = StatusSuggestionService(getRepository(false))
        assertThrows<InvalidIdException> { service.getById(testId) }
    }
}
