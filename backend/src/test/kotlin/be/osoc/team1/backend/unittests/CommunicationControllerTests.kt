package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.CommunicationController
import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.TypeEnum
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.UUID

@WebMvcTest(CommunicationController::class)
class CommunicationControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var communicationService: CommunicationService

    @MockkBean
    private lateinit var studentService: StudentService

    private val testId = UUID.randomUUID()
    private val testStudent = Student("Jitse", "Willaert")
    private val testCommunication = Communication("test message", TypeEnum.Email, testStudent)
    private val objectMapper = ObjectMapper()
    private val jsonRepresentation = objectMapper.writeValueAsString(testCommunication)

    @Test
    fun `getCommunicationsByStudentId succeeds if student with given id exists`() {
        every { studentService.getStudentById(testId) } returns testStudent
        mockMvc.perform(MockMvcRequestBuilders.get("/communications/$testId"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("[]"))
    }

    @Test
    fun `getCommunicationsByStudentId returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.getStudentById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(MockMvcRequestBuilders.get("/communications/$differentId"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `postCommunication succeeds if student with given id exists`() {
        val databaseId = UUID.randomUUID()
        every { communicationService.postCommunication(any()) } returns databaseId
        every { studentService.addCommunicationToStudent(testId, any()) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.post("/communications/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("\"$databaseId\""))
    }

    @Test
    fun `postCommunication returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { communicationService.postCommunication(any()) } returns differentId
        every { studentService.addCommunicationToStudent(differentId, any()) }.throws(InvalidIdException())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/communications/$differentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}
