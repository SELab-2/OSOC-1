package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.CommunicationController
import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.CommunicationTypeEnum
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.CommunicationService
import be.osoc.team1.backend.services.StudentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
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

    private val testId = UUID.randomUUID()
    private val testEdition = "testEdition"
    private val editionUrl = "/$testEdition/communications"
    private val testCommunication = Communication("test message", CommunicationTypeEnum.Email)
    private val objectMapper = ObjectMapper()
    private val jsonRepresentation = objectMapper.writeValueAsString(testCommunication)

    @Test
    fun `getCommunicationById returns communication if communication with given id exists`() {
        every { communicationService.getCommunicationById(testId, testEdition) } returns testCommunication
        mockMvc.perform(get("$editionUrl/$testId")).andExpect(status().isOk)
            .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getCommunicationById returns 404 Not Found if communication with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { communicationService.getCommunicationById(differentId, testEdition) }.throws(InvalidIdException())
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
}
