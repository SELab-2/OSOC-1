package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.CommunicationController
import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.CommunicationDTO
import be.osoc.team1.backend.entities.CommunicationTypeEnum
import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.CommunicationService
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.OsocUserDetailService
import be.osoc.team1.backend.services.StudentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
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

    private val testStudent = Student("firstname", "lastname")
    private val testId = testStudent.id
    private val testEdition = "testEdition"
    private val editionUrl = "/$testEdition/communications"
    private val testCommunication = Communication("test message", CommunicationTypeEnum.Email, testEdition, testStudent)
    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())
    private val jsonRepresentation = objectMapper.writeValueAsString(testCommunication)
    private val jsonRepresentationDTO = objectMapper.writeValueAsString(CommunicationDTO("test message", CommunicationTypeEnum.Email))

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
        every { studentService.getStudentById(any(), testEdition) } returns testStudent
        every { studentService.addCommunicationToStudent(any(), testEdition) } just Runs
        mockMvc.perform(
            post("$editionUrl/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentationDTO)
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("message").value("test message"))
            .andExpect(jsonPath("type").value("Email"))
    }

    @Test
    fun `createCommunication returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.getStudentById(differentId, testEdition) }.throws(InvalidIdException())
        mockMvc.perform(
            post("$editionUrl/$differentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteCommunication removes the communication if it exists`() {
        val differentId = UUID.randomUUID()
        every { studentService.removeCommunicationFromStudent(differentId, testEdition) } just Runs
        mockMvc.perform(
            delete("$editionUrl/$differentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteCommunication returns 404 Not Found if the communication does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.removeCommunicationFromStudent(differentId, testEdition) }.throws(InvalidIdException())
        mockMvc.perform(
            delete("$editionUrl/$differentId")
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
        every { editionService.getEdition(any()) } returns Edition(testEdition, false)
        every { studentService.addCommunicationToStudent(any(), testEdition) } just Runs
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
