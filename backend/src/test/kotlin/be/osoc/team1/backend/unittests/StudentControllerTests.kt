package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.StudentController
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Skill
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
import be.osoc.team1.backend.util.TallyDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.nio.file.Files
import java.nio.file.Paths
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
    private val defaultStatusFilter =
        listOf(StatusEnum.Yes, StatusEnum.No, StatusEnum.Maybe, StatusEnum.Undecided)
    private val testSuggestion = StatusSuggestion(coachId, SuggestionEnum.Yes, "test motivation")

    @BeforeEach
    fun beforeEach() {
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userDetailService.getUserFromPrincipal(any()) } returns testCoach
    }

    @Test
    fun `getAllStudents should not fail`() {
        every { studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "", true, testCoach) } returns
            emptyList()
        mockMvc.perform(get("/students").principal(TestingAuthenticationToken(null, null))).andExpect(status().isOk)
    }

    @Test
    fun `getAllStudents paging returns the correct amount`() {
        val testList = listOf(testStudent)
        every { studentService.getAllStudents(0, 1, "id", defaultStatusFilter, "", true, testCoach) } returns
            testList
        mockMvc.perform(get("/students?pageNumber=0&pageSize=1").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList)))
    }

    @Test
    fun `getAllStudents status filtering parses the correct statuses`() {
        val testStudent1 = Student("L1", "VC")
        testStudent1.status = StatusEnum.Yes
        val testStudent2 = Student("L2", "VC")
        testStudent2.status = StatusEnum.No
        val testStudent3 = Student("L3", "VC")
        testStudent3.status = StatusEnum.Maybe
        val testStudent4 = Student("L4", "VC")
        testStudent4.status = StatusEnum.Undecided
        val allStudents = listOf(testStudent1, testStudent2, testStudent3, testStudent4)
        every {
            studentService.getAllStudents(0, 50, "id", listOf(StatusEnum.Yes), "", true, testCoach)
        } returns listOf(testStudent1)
        every {
            studentService.getAllStudents(0, 50, "id", listOf(StatusEnum.No), "", true, testCoach)
        } returns listOf(testStudent2)
        every {
            studentService.getAllStudents(0, 50, "id", listOf(StatusEnum.Maybe), "", true, testCoach)
        } returns listOf(testStudent3)
        every {
            studentService.getAllStudents(0, 50, "id", listOf(StatusEnum.Undecided), "", true, testCoach)
        } returns listOf(testStudent4)
        every {
            studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "", true, testCoach)
        } returns allStudents
        mockMvc.perform(get("/students?status=Yes").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(listOf(testStudent1))))
        mockMvc.perform(get("/students?status=No").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(listOf(testStudent2))))
        mockMvc.perform(get("/students?status=Maybe").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(listOf(testStudent3))))
        mockMvc.perform(get("/students?status=Undecided").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(listOf(testStudent4))))
        mockMvc.perform(get("/students?status=Yes,No,Maybe,Undecided").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(allStudents)))
    }

    @Test
    fun `getAllStudents name filtering parses the correct name`() {
        val testList = listOf(Student("_", "_"))
        val testList2 = listOf(Student("_2", "_2"))
        every {
            studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "lars", true, testCoach)
        } returns testList
        every {
            studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "lars test", true, testCoach)
        } returns testList2
        // tests the url parsing + with url encoding
        mockMvc.perform(get("/students?name=lars").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList)))
        mockMvc.perform(get("/students?name=lars%20test").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList2)))
    }

    @Test
    fun `getAllStudents include filtering parses the boolean correctly`() {
        val testList = listOf(Student("test", "testie"))
        every { studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "", false, testCoach) } returns
            testList
        mockMvc.perform(get("/students?includeSuggested=false").principal(TestingAuthenticationToken(null, null)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList)))
    }

    @Test
    fun `getStudentById returns student if student with given id exists`() {
        val jsonRepresentation = objectMapper.writeValueAsString(testStudent)

        every { studentService.getStudentById(studentId) } returns testStudent
        mockMvc.perform(get("/students/$studentId"))
            .andExpect(status().isOk)
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

    private fun jsonNodeFromFile(filename: String): JsonNode {
        val studentJsonForm =
            Files.readAllBytes(Paths.get(this::class.java.classLoader.getResource(filename).toURI()))
        return objectMapper.readTree(studentJsonForm)
    }

    private fun removeFieldFromTallyForm(rootNode: JsonNode, key: String) {
        val iter = rootNode.get("data").get("fields").elements()
        while (iter.hasNext()) {
            val field = iter.next()
            if (field.get("key").asText() == key)
                iter.remove()
        }
    }

    private fun setFieldValueFromTallyForm(rootNode: JsonNode, key: String, value: String?) {
        val iter = rootNode.get("data").get("fields").elements()
        while (iter.hasNext()) {
            val field = iter.next()
            if (field.get("key").asText() == key) {
                (field as ObjectNode).put("value", value)
            }
        }
    }

    @Test
    fun `addStudent should return created student`() {
        val node = jsonNodeFromFile("student_test_form.json")
        val slot = slot<Student>()
        every { studentService.addStudent(capture(slot)) } returns testStudent
        val mvcResult =
            mockMvc.perform(
                post("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(node))
            )
                .andExpect(status().isCreated)
                .andReturn()

        val capturedStudent = slot.captured
        assertEquals("Maarten", capturedStudent.firstName)
        assertEquals("Steevens", capturedStudent.lastName)
        assert(capturedStudent.skills.contains(Skill("Back-end developer")))
        assertEquals(true, capturedStudent.alumn)

        val locationHeader = mvcResult.response.getHeader("Location")
        assert(locationHeader!!.endsWith("/students/${testStudent.id}"))
    }

    private fun testInvalidTallyForm(node: JsonNode) {
        every { studentService.addStudent(any()) } returns testStudent
        mockMvc.perform(
            post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(node))
        ).andExpect(status().isBadRequest)
    }
    @Test
    fun `addStudent should fail when firstname question is not given`() {
        val node = jsonNodeFromFile("student_test_form.json")
        removeFieldFromTallyForm(node, TallyDeserializer.TallyKeys.firstnameQuestion)

        testInvalidTallyForm(node)
    }

    @Test
    fun `addStudent should fail when answer to firstname question is empty`() {
        val node = jsonNodeFromFile("student_test_form.json")
        setFieldValueFromTallyForm(node, TallyDeserializer.TallyKeys.firstnameQuestion, null)

        testInvalidTallyForm(node)
    }

    @Test
    fun `addStudent should fail when lastname question is not given`() {
        val node = jsonNodeFromFile("student_test_form.json")
        removeFieldFromTallyForm(node, TallyDeserializer.TallyKeys.lastnameQuestion)

        testInvalidTallyForm(node)
    }

    @Test
    fun `addStudent should fail when alumni question is not given`() {
        val node = jsonNodeFromFile("student_test_form.json")
        removeFieldFromTallyForm(node, TallyDeserializer.TallyKeys.alumnQuestion)

        testInvalidTallyForm(node)
    }

    @Test
    fun `addStudent should fail when skill question is not given`() {
        val node = jsonNodeFromFile("student_test_form.json")
        removeFieldFromTallyForm(node, TallyDeserializer.TallyKeys.skillQuestion)

        testInvalidTallyForm(node)
    }

    @Test
    fun `addStudent should fail when an option is used as a value not present in the options list with MULTIPLE_CHOICE`() {
        val node = jsonNodeFromFile("student_test_form.json")
        setFieldValueFromTallyForm(node, TallyDeserializer.TallyKeys.alumnQuestion, "689451da-305b-451a-8039-c748ff06ec83")

        testInvalidTallyForm(node)
    }

    @Test
    fun `setStudentStatus succeeds when student with given id exists`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(studentId, status) } just Runs
        mockMvc.perform(
            post("/students/$studentId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `setStudentStatus returns 404 Not Found if student with given id does not exist`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(studentId, status) }.throws(InvalidIdException())
        mockMvc.perform(
            post("/students/$studentId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion succeeds when student with given id exists`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) } just Runs
        mockMvc.perform(
            post("/students/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `addStudentStatusSuggestion returns 404 Not Found if student with given id does not exist`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) }
            .throws(InvalidStudentIdException())
        mockMvc.perform(
            post("/students/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion returns 403 Forbidden if coach already made suggestion for student`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) }
            .throws(ForbiddenOperationException())
        mockMvc.perform(
            post("/students/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `addStudentStatusSuggestion returns 404 Not Found if coach doesn't exist`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) }
            .throws(InvalidUserIdException())

        mockMvc.perform(
            post("/students/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion returns 401 if we try creating a suggestion on behalf of another user`() {
        every { userDetailService.getUserFromPrincipal(any()) } returns
            User("other user", "email", Role.Coach, "password")

        mockMvc.perform(
            post("/students/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `deleteStudentStatusSuggestion succeeds when student, suggestion and coach exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) } just Runs

        mockMvc.perform(
            delete("/students/$studentId/suggestions/$coachId")
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 404 Not Found if student doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) }
            .throws(InvalidStudentIdException())

        mockMvc.perform(
            delete("/students/$studentId/suggestions/$coachId")
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 400 Bad Request if suggestion doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) }
            .throws(FailedOperationException())

        mockMvc.perform(
            delete("/students/$studentId/suggestions/$coachId")
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 404 Not Found if coach doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) }
            .throws(InvalidUserIdException())

        mockMvc.perform(
            delete("/students/$studentId/suggestions/$coachId")
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 401 if we try deleting a suggestion made by another user`() {
        every { userDetailService.getUserFromPrincipal(any()) } returns
            User("other user", "email", Role.Coach, "password")

        mockMvc.perform(
            delete("/students/$studentId/suggestions/$coachId")
                .principal(TestingAuthenticationToken(null, null))
        )
            .andExpect(status().isUnauthorized)
    }
}
