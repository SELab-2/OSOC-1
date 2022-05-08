package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.CommunicationController
import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.CommunicationTypeEnum
import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.CommunicationService
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.OsocUserDetailService
import be.osoc.team1.backend.services.StudentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@UnsecuredWebMvcTest(CommunicationController::class)
class CommunicationControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var communicationService: CommunicationService

    @MockkBean
    private lateinit var studentService: StudentService

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
    private val testEdition = "testEdition"
    private val editionUrl = "/$testEdition/communications"
    private val testCommunication = Communication("test message", CommunicationTypeEnum.Email)
    private val objectMapper = ObjectMapper()
    private val jsonRepresentation = objectMapper.writeValueAsString(testCommunication)

    private val authenticatedAdmin = User("name", "email", Role.Admin, "password")
    private val activeEdition = Edition("activeEdition", true)

    @BeforeEach
    fun setup() {
        SecurityContextHolder.setContext(securityContext)
        every { securityContext.authentication } returns authentication
        every { osocUserDetailService.getUserFromPrincipal(any()) } returns authenticatedAdmin
        every { editionService.getEdition(any()) } returns activeEdition
    }

    @Test
    fun `getCommunicationById returns communication if communication with given id exists`() {
        every { communicationService.getById(testId) } returns testCommunication
        every { editionService.getEdition(any()) } returns Edition(testEdition, true)
        mockMvc.perform(get("$editionUrl/$testId").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getCommunicationById returns 404 Not Found if communication with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { communicationService.getById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("$editionUrl/$differentId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createCommunication succeeds if student with given id exists`() {
        every { communicationService.createCommunication(any()) } returns testCommunication
        every { studentService.addCommunicationToStudent(testId, any(), testEdition) } just Runs
        mockMvc.perform(
            post("$editionUrl/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isCreated).andExpect(content().string(jsonRepresentation))
    }

    @Test
    fun `createCommunication returns 404 Not Found if student with given id does not exist`() {
        every { communicationService.createCommunication(any()) } returns testCommunication
        val differentId = UUID.randomUUID()
        every { studentService.addCommunicationToStudent(differentId, any(), testEdition) }.throws(InvalidIdException())
        mockMvc.perform(
            post("$editionUrl/$differentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `Inactive editions can be accessed by admins`() {
        every { communicationService.getById(testId) } returns testCommunication
        every { editionService.getEdition(any()) } returns Edition(testEdition, false)
        every { osocUserDetailService.getUserFromPrincipal(any()) } returns User(
            "name",
            "email",
            Role.Admin,
            "password"
        )
        mockMvc.perform(get("$editionUrl/$testId").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
    }

    @Test
    fun `Inactive editions cannot be accessed by others`() {
        every { communicationService.getById(testId) } returns testCommunication
        every { editionService.getEdition(any()) } returns Edition(testEdition, false)
        every { osocUserDetailService.getUserFromPrincipal(any()) } returns User(
            "name",
            "email",
            Role.Coach,
            "password"
        )
        mockMvc.perform(get("$editionUrl/$testId").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `Gets and deletes are the only allowed HTTP methods on inactive editions`() {
        every { communicationService.getById(testId) } returns testCommunication
        every { communicationService.createCommunication(any()) } returns testCommunication
        every { editionService.getEdition(any()) } returns Edition(testEdition, false)
        every { studentService.addCommunicationToStudent(testId, any(), testEdition) } just Runs
        every { osocUserDetailService.getUserFromPrincipal(any()) } returns User(
            "name",
            "email",
            Role.Admin,
            "password"
        )
        mockMvc.perform(get("$editionUrl/$testId").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
        mockMvc.perform(
            post("$editionUrl/$testId").principal(TestingAuthenticationToken(null, null))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isForbidden)
    }
}
