package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.repositories.SkillRepository
import be.osoc.team1.backend.services.SkillService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SkillServiceTests {

    @Test
    fun `getAll succeeds`() {
        val repository: SkillRepository = mockk()
        val skill = Skill("Back-end")
        val skills = listOf(skill)
        every { repository.findAll() } returns skills
        val service = SkillService(repository)
        Assertions.assertEquals(skills, service.getAll())
    }
}
