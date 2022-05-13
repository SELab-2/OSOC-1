package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.StatusSuggestionController
import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.SuggestionEnum
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.OsocUserDetailService
import be.osoc.team1.backend.services.StatusSuggestionService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

@UnsecuredWebMvcTest(StatusSuggestionController::class)
class StatusSuggestionControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var statusSuggestionService: StatusSuggestionService

    // These MockkBean are necessary because the BaseController uses these under the hood
    @MockkBean
    private lateinit var editionService: EditionService
    @MockkBean
    private lateinit var osocUserDetailService: OsocUserDetailService
    @MockkBean
    private lateinit var authentication: Authentication
    @MockkBean
    private lateinit var securityContext: SecurityContext

    private val testId = UUID.randomUUID()
    private val coachId = UUID.randomUUID()
    private val testStatusSuggestion = StatusSuggestion(coachId, SuggestionEnum.Yes, "motivation")
    private val objectMapper = ObjectMapper()

    private val authenticatedAdmin = User("name", "email", Role.Admin, "password")
    @BeforeEach
    fun beforeEach() {
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))
        SecurityContextHolder.setContext(securityContext)
        every { securityContext.authentication } returns authentication
        every { osocUserDetailService.getUserFromPrincipal(any()) } returns authenticatedAdmin
    }

    @Test
    fun `getStatusSuggestionById returns statusSuggestion if statusSuggestion with given id exists`() {
        val jsonRepresentation = objectMapper.writeValueAsString(testStatusSuggestion)
        every { statusSuggestionService.getById(testId) } returns testStatusSuggestion
        every { editionService.getEdition(any()) } returns Edition("edition", true)
        mockMvc.perform(get("/statusSuggestions/$testId")).andExpect(status().isOk)
            .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getStatusSuggestionById returns 404 Not Found if statusSuggestion with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { statusSuggestionService.getById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("/statusSuggestions/$differentId"))
            .andExpect(status().isNotFound)
    }
}
