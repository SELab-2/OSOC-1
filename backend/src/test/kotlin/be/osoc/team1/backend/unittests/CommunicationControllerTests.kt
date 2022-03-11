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
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.bind.Bindable.listOf
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

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
        val mutableList: MutableList<Communication> = ArrayList()
        mutableList.add(testCommunication)
        every { studentService.getStudentById(testId).communications } returns mutableList
        mockMvc.perform(get("/communications/$testId"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("test message")))
    }

    @Test
    fun `getCommunicationsByStudentId returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.getStudentById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("/communications/$differentId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createCommunication succeeds if student with given id exists`() {
        val databaseId = UUID.randomUUID()
        every { communicationService.createCommunication(any()) } returns databaseId
        every { studentService.addCommunicationToStudent(testId, any()) } just Runs
        mockMvc.perform(
            post("/communications/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isCreated)
    }

    @Test
    fun `createCommunication returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { communicationService.createCommunication(any()) } returns differentId
        every { studentService.addCommunicationToStudent(differentId, any()) }.throws(InvalidIdException())
        mockMvc.perform(
            post("/communications/$differentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isNotFound)
    }
}
