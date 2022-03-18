package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.StudentController
import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.Student
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
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(StudentController::class)
class AuthorizationTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var studentService: StudentService

    private val testId = UUID.randomUUID()
    private val testStudent = Student("Tom", "Alard")
    private val objectMapper = ObjectMapper()
    private val jsonRepresentation = objectMapper.writeValueAsString(testStudent)
    private val testMotivation = "test motivation"

    @Test
    fun `GET returns 403 when not logged in`() {
        every { studentService.getAllStudents() } returns emptyList()
        mockMvc.perform(get("/students")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `GET succeeds when logged in as user`() {
        every { studentService.getAllStudents() } returns emptyList()
        mockMvc.perform(get("/students"))
            .andExpect(status().isOk)
    }

    @Test
    fun `POST returns 403 when not logged in`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(testId, status) } just Runs
        mockMvc.perform(
            post("/students/$testId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `POST returns 403 when logged in as user`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(testId, status) } just Runs
        mockMvc.perform(
            post("/students/$testId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `POST succeeds when logged in as admin`() {
        val databaseId = UUID.randomUUID()
        every { studentService.addStudent(any()) } returns databaseId
        val mvcResult = mockMvc.perform(
            post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isCreated).andReturn()
        val locationHeader = mvcResult.response.getHeader("Location")
        assert(locationHeader!!.endsWith("/students/$databaseId"))
    }
}
