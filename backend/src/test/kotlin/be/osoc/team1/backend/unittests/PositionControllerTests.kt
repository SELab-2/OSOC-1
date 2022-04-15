package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.PositionController
import be.osoc.team1.backend.entities.*
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.PositionService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID


@UnsecuredWebMvcTest(PositionController::class)
class PositionControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var positionService: PositionService

    private val testId = UUID.randomUUID()
    private val testSkill = Skill("Test")
    private val testPosition = Position(testSkill, 2)
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun beforeEach() {
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))
    }

    @Test
    fun `getPositionById returns position if position with given id exists`() {
        val jsonRepresentation = objectMapper.writeValueAsString(testPosition)
        every { positionService.getPositionById(testId) } returns testPosition
        mockMvc.perform(get("/positions/$testId")).andExpect(status().isOk)
                .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getPositionById returns 404 Not Found if position with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { positionService.getPositionById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("/positions/$differentId"))
                .andExpect(status().isNotFound)
    }
}
