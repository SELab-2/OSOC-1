package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.exceptions.InvalidPositionIdException
import be.osoc.team1.backend.repositories.PositionRepository
import be.osoc.team1.backend.services.PositionService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class PositionServiceTests {
    private val testId = UUID.randomUUID()
    private val testSkill = Skill("Test")
    private val testPosition = Position(testSkill, 2)

    private fun getRepository(positionAlreadyExists: Boolean): PositionRepository {
        val repository: PositionRepository = mockk()
        every { repository.existsById(any()) } returns positionAlreadyExists
        every { repository.findByIdOrNull(any()) } returns if (positionAlreadyExists) testPosition else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns testPosition
        return repository
    }

    @Test
    fun `getPositionById succeeds when position with id exists`() {
        val service = PositionService(getRepository(true))
        Assertions.assertEquals(testPosition, service.getPositionById(testId))
    }

    @Test
    fun `getPositionById fails when no position with that id exists`() {
        val service = PositionService(getRepository(false))
        assertThrows<InvalidPositionIdException> { service.getPositionById(testId) }
    }
}
