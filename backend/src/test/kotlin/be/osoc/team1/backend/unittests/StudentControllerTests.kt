package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.StudentController
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.StatusEnum
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.SuggestionEnum
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.exceptions.InvalidStudentIdException
import be.osoc.team1.backend.exceptions.InvalidUserIdException
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

// See: https://www.baeldung.com/kotlin/spring-boot-testing
@UnsecuredWebMvcTest(StudentController::class)
class StudentControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var studentService: StudentService

    @MockkBean
    private lateinit var userDetailService: OsocUserDetailService

    private val studentId = UUID.randomUUID()
    private val testCoach = User("coach", "email", Role.Coach, "password")
    private val coachId = testCoach.id
    private val testStudent = Student("Tom", "Alard")
    private val objectMapper = ObjectMapper()
    private val jsonRepresentation = objectMapper.writeValueAsString(testStudent)
    private val testSuggestion = StatusSuggestion(coachId, SuggestionEnum.Yes, "test motivation")

    @BeforeEach
    fun beforeEach() {
        every { userDetailService.getUserFromPrincipal(any()) } returns testCoach
    }

    @Test
    fun `getAllStudents should not fail`() {
        every { studentService.getAllStudents(0, 50, "id") } returns emptyList()
        mockMvc.perform(get("/students"))
            .andExpect(status().isOk)
    }

    @Test
    fun `getAllStudents paging returns the correct amount`() {
        val testList = listOf(testStudent)
        every { studentService.getAllStudents(0, 1, "id") } returns testList
        mockMvc.perform(get("/students?pageNumber=0&pageSize=1"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList)))
    }

    @Test
    fun `getStudentById returns student if student with given id exists`() {
        every { studentService.getStudentById(studentId) } returns testStudent
        mockMvc.perform(get("/students/$studentId")).andExpect(status().isOk)
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
        every { studentService.deleteStudentById(studentId) } just Runs
        mockMvc.perform(delete("/students/$studentId")).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteStudentById returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.deleteStudentById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(delete("/students/$differentId")).andExpect(status().isNotFound)
    }

    @Test
    fun `addStudent should return created student`() {
        every { studentService.addStudent(any()) } returns testStudent
        val mvcResult = mockMvc.perform(
            post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isCreated).andReturn()
        val locationHeader = mvcResult.response.getHeader("Location")
        assert(locationHeader!!.endsWith("/students/${testStudent.id}"))
    }

    @Test
    fun `setStudentStatus succeeds when student with given id exists`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(studentId, status) } just Runs
        mockMvc.perform(
            post("/students/$studentId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `setStudentStatus returns 404 Not Found if student with given id does not exist`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(studentId, status) }.throws(InvalidIdException())
        mockMvc.perform(
            post("/students/$studentId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion succeeds when student with given id exists`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) } just Runs
        mockMvc.perform(
            post("/students/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(TestingAuthenticationToken(null, null))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `addStudentStatusSuggestion returns 404 Not Found if student with given id does not exist`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) }.throws(InvalidStudentIdException())
        mockMvc.perform(
            post("/students/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(TestingAuthenticationToken(null, null))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion returns 403 Forbidden if coach already made suggestion for student`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) }.throws(ForbiddenOperationException())
        mockMvc.perform(
            post("/students/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(TestingAuthenticationToken(null, null))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `addStudentStatusSuggestion returns 404 Not Found if coach doesn't exist`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) }.throws(InvalidUserIdException())

        mockMvc.perform(
            post("/students/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(TestingAuthenticationToken(null, null))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentStatusSuggestion succeeds when student, suggestion and coach exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) } just Runs

        mockMvc.perform(
            delete("/students/$studentId/suggestions/$coachId")
                .principal(TestingAuthenticationToken(null, null))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 404 Not Found if student doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) }.throws(InvalidStudentIdException())

        mockMvc.perform(
            delete("/students/$studentId/suggestions/$coachId")
                .principal(TestingAuthenticationToken(null, null))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 400 Bad Request if suggestion doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) }.throws(FailedOperationException())

        mockMvc.perform(
            delete("/students/$studentId/suggestions/$coachId")
                .principal(TestingAuthenticationToken(null, null))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 404 Not Found if coach doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) }.throws(InvalidUserIdException())

        mockMvc.perform(
            delete("/students/$studentId/suggestions/$coachId")
                .principal(TestingAuthenticationToken(null, null))
        ).andExpect(status().isNotFound)
    }
}
