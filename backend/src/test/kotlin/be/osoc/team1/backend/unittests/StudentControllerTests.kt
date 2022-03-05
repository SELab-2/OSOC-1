package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.StudentController
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.StudentService
import com.google.gson.Gson
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

// See: https://www.baeldung.com/kotlin/spring-boot-testing
@WebMvcTest(StudentController::class)
class StudentControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var studentService: StudentService

    private val testId = UUID.randomUUID()
    private val testStudent = Student(testId, "Tom", "Alard")
    private val jsonRepresentation = Gson().toJson(testStudent)

    @Test
    fun `getAllStudents should not fail`() {
        every { studentService.getAllStudents() } returns emptyList()
        mockMvc.perform(get("/students")).andExpect(status().isOk)
    }

    @Test
    fun `getStudentById returns student if student with given id exists`() {
        every { studentService.getStudentById(testId) } returns testStudent
        mockMvc.perform(get("/students/$testId")).andExpect(status().isOk).
            andExpect(content().json(jsonRepresentation))
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
        mockMvc.perform(delete("/students/$testId")).andExpect(status().isOk)
    }

    @Test
    fun `deleteStudentById returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.deleteStudentById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(delete("/students/$differentId")).andExpect(status().isNotFound)
    }

    @Test
    fun `putStudent should not fail`() {
        val databaseId = UUID.randomUUID()
        every { studentService.putStudent(any()) } returns databaseId
        mockMvc.perform(
            put("/students/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isOk).andExpect(content().string("\"$databaseId\""))
    }
}