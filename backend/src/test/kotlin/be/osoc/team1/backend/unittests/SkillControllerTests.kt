package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.SkillController
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.services.SkillService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@UnsecuredWebMvcTest(SkillController::class)
class SkillControllerTests(@Autowired private val mockMvc: MockMvc) {
    @MockkBean
    private lateinit var skillService: SkillService

    @Test
    fun `getAll succeeds`() {
        val skill = Skill("Back-end")
        val skills = listOf(skill)
        every { skillService.getAll() } returns skills
        mockMvc.perform(MockMvcRequestBuilders.get("/skills"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(ObjectMapper().writeValueAsString(skills)))
    }
}
