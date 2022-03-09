package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.StudentController
import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.SuggestionEnum
import be.osoc.team1.backend.exceptions.InvalidIdException
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

// See: https://www.baeldung.com/kotlin/spring-boot-testing
@WebMvcTest(StudentController::class)
class StudentControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var studentService: StudentService

    private val testId = UUID.randomUUID()
    private val testStudent = Student("Tom", "Alard")
    private val objectMapper = ObjectMapper()
    private val jsonRepresentation = objectMapper.writeValueAsString(testStudent)
    private val testMotivation = "test motivation"

    @Test
    fun `getAllStudents should not fail`() {
        every { studentService.getAllStudents() } returns emptyList()
        mockMvc.perform(get("/students")).andExpect(status().isOk)
    }

    @Test
    fun `getStudentById returns student if student with given id exists`() {
        every { studentService.getStudentById(testId) } returns testStudent
        mockMvc.perform(get("/students/$testId")).andExpect(status().isOk)
            .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getStudentById returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.getStudentById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("/students/$differentId")).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentById succeeds if student with given id exists`() {
        every { studentService.deleteStudentById(testId) } just Runs
        mockMvc.perform(delete("/students/$testId")).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteStudentById returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.deleteStudentById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(delete("/students/$differentId")).andExpect(status().isNotFound)
    }

    @Test
    fun `addStudent should not fail`() {
        val databaseId = UUID.randomUUID()
        every { studentService.addStudent(any()) } returns databaseId
        mockMvc.perform(
            post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isOk).andExpect(content().string("\"$databaseId\""))
    }

    @Test
    fun `setStudentStatus succeeds when student with given id exists`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(testId, status) } just Runs
        mockMvc.perform(
            post("/students/$testId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `setStudentStatus returns 404 Not Found if student with given id does not exist`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(testId, status) }.throws(InvalidIdException())
        mockMvc.perform(
            post("/students/$testId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion succeeds when student with given id exists`() {
        val suggestion = SuggestionEnum.Yes
        val statusSuggestion = StatusSuggestion(suggestion, testMotivation)
        every {
            studentService.addStudentStatusSuggestion(testId, statusSuggestion.status, statusSuggestion.motivation)
        } just Runs
        mockMvc.perform(
            post("/students/$testId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusSuggestion))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `addStudentStatusSuggestion returns 404 Not Found if student with given id does not exist`() {
        val suggestion = SuggestionEnum.Yes
        val statusSuggestion = StatusSuggestion(suggestion, testMotivation)
        every {
            studentService.addStudentStatusSuggestion(testId, statusSuggestion.status, statusSuggestion.motivation)
        }.throws(InvalidIdException())
        mockMvc.perform(
            post("/students/$testId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusSuggestion))
        ).andExpect(status().isNotFound)
    }
}
