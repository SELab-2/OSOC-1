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
import be.osoc.team1.backend.services.PagedCollection
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
import org.springframework.data.domain.Sort
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
    private val testEdition = "testEdition"
    private val testStudent = Student("Tom", "Alard", testEdition)
    private val objectMapper = ObjectMapper()
    private val editionUrl = "/$testEdition/students"
    private val defaultPrincipal = TestingAuthenticationToken(null, null)
    private val defaultSort = Sort.by("id")
    private val testSuggestion = StatusSuggestion(coachId, SuggestionEnum.Yes, "test motivation")

    @BeforeEach
    fun beforeEach() {
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))
        every { userDetailService.getUserFromPrincipal(any()) } returns testCoach
    }

    @Test
    fun `getAllStudents should not fail`() {
        every { studentService.getAllStudents(defaultSort, testEdition) } returns emptyList()
        mockMvc.perform(get(editionUrl).principal(defaultPrincipal)).andExpect(status().isOk)
    }

    @Test
    fun `getAllStudents paging returns the correct amount`() {
        val allStudents = listOf(
            testStudent,
            Student("Foo", "Bar"),
            Student("Fooo", "Baar")
        )
        val expected = listOf(testStudent)
        every { studentService.getAllStudents(defaultSort, testEdition) } returns allStudents
        mockMvc.perform(get("$editionUrl?pageNumber=0&pageSize=1").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(expected, 3))))
    }

    @Test
    fun `getAllStudents status filtering parses the correct statuses and filters accordingly`() {
        val testStudent1 = Student("L1", "VC", testEdition)
        testStudent1.status = StatusEnum.Yes
        val testStudent2 = Student("L2", "VC", testEdition)
        testStudent2.status = StatusEnum.No
        val testStudent3 = Student("L3", "VC", testEdition)
        testStudent3.status = StatusEnum.Maybe
        val testStudent4 = Student("L4", "VC", testEdition)
        testStudent4.status = StatusEnum.Undecided
        val allStudents = listOf(testStudent1, testStudent2, testStudent3, testStudent4)
        every { studentService.getAllStudents(defaultSort, testEdition) } returns allStudents
        mockMvc.perform(get("$editionUrl?status=Yes").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(listOf(testStudent1), 1))))
        mockMvc.perform(get("$editionUrl?status=No").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(listOf(testStudent2), 1))))
        mockMvc.perform(get("$editionUrl?status=Maybe").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(listOf(testStudent3), 1))))
        mockMvc.perform(get("$editionUrl?status=Undecided").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(listOf(testStudent4), 1))))
        mockMvc.perform(get("$editionUrl?status=Yes,No,Maybe,Undecided").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(allStudents, 4))))
    }

    @Test
    fun `getAllStudents paging with filtering returns the correct amount`() {
        val testStudent1 = Student("ATestoon", "Tamzia", testEdition)
        testStudent1.status = StatusEnum.Maybe
        val testStudent2 = Student("BTestien", "Tamzia", testEdition)
        testStudent2.status = StatusEnum.Yes
        val testStudent3 = Student("CTestaan", "Tamzia", testEdition)
        testStudent3.status = StatusEnum.Yes
        val allStudents = listOf(testStudent1, testStudent2, testStudent3)

        every { studentService.getAllStudents(Sort.by("name"), testEdition) } returns allStudents
        mockMvc.perform(get("$editionUrl?status=Yes&sortBy=name&pageSize=2").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(
                content().json(objectMapper.writeValueAsString(PagedCollection(listOf(testStudent2, testStudent3), 2)))
            )
    }

    @Test
    fun `getAllStudents name filtering returns only students with those names`() {
        val testStudent = Student("Lars", "Cauter", testEdition)
        val testStudent2 = Student("Sral", "Retuac", testEdition)
        val testStudent3 = Student("Arsl", "Auterc", testEdition)
        val testStudent4 = Student("Rsla", "Uterca", testEdition)
        val allStudents = listOf(testStudent, testStudent2, testStudent3, testStudent4)
        every { studentService.getAllStudents(defaultSort, testEdition) } returns allStudents
        mockMvc.perform(get("$editionUrl?name=lars").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(listOf(testStudent), 1))))
        mockMvc.perform(get("$editionUrl?name=ars").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(
                content().json(objectMapper.writeValueAsString(PagedCollection(listOf(testStudent, testStudent3), 2)))
            )
        mockMvc.perform(get("$editionUrl?name=uter").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    objectMapper.writeValueAsString(PagedCollection(listOf(testStudent, testStudent3, testStudent4), 3))
                )
            )
        mockMvc.perform(get("$editionUrl?name=").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(allStudents, 4))))
    }

    @Test
    fun `getAllStudents include filtering works`() {
        val testStudent = Student("Lars", "Van", testEdition)
        testStudent.statusSuggestions.add(StatusSuggestion(testCoach.id, SuggestionEnum.Yes, "Nice!"))
        every { studentService.getAllStudents(defaultSort, testEdition) } returns listOf(testStudent)
        mockMvc.perform(get("$editionUrl?includeSuggested=false").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(listOf<Student>(), 0))))
        mockMvc.perform(get("$editionUrl?includeSuggested=true").principal(defaultPrincipal))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(listOf(testStudent), 1))))
    }

    @Test
    fun `getStudentById returns student if student with given id exists`() {
        val jsonRepresentation = objectMapper.writeValueAsString(testStudent)

        every { studentService.getStudentById(studentId, testEdition) } returns testStudent
        mockMvc.perform(get("$editionUrl/$studentId"))
            .andExpect(status().isOk)
            .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getStudentById returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.getStudentById(differentId, testEdition) }.throws(InvalidIdException())
        mockMvc.perform(get("$editionUrl/$differentId")).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentById succeeds if student with given id exists`() {
        every { studentService.deleteStudentById(studentId) } just Runs
        mockMvc.perform(delete("$editionUrl/$studentId")).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteStudentById returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.deleteStudentById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(delete("$editionUrl/$differentId")).andExpect(status().isNotFound)
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
                post(editionUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(node))
            ).andExpect(status().isCreated).andReturn()

        val capturedStudent = slot.captured
        assertEquals("Maarten", capturedStudent.firstName)
        assertEquals("Steevens", capturedStudent.lastName)
        assert(capturedStudent.skills.contains(Skill("Back-end developer")))
        assertEquals(true, capturedStudent.alumn)
        assertEquals(false, capturedStudent.possibleStudentCoach)

        val locationHeader = mvcResult.response.getHeader("Location")
        assert(locationHeader!!.endsWith("$editionUrl/${testStudent.id}"))
    }

    private fun testInvalidTallyForm(node: JsonNode) {
        every { studentService.addStudent(any()) } returns testStudent
        mockMvc.perform(
            post(editionUrl)
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
        setFieldValueFromTallyForm(
            node,
            TallyDeserializer.TallyKeys.alumnQuestion,
            "689451da-305b-451a-8039-c748ff06ec83"
        )

        testInvalidTallyForm(node)
    }

    @Test
    fun `setStudentStatus succeeds when student with given id exists`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(studentId, status, testEdition) } just Runs
        mockMvc.perform(
            post("$editionUrl/$studentId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `setStudentStatus returns 404 Not Found if student with given id does not exist`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(studentId, status, testEdition) }.throws(InvalidIdException())
        mockMvc.perform(
            post("$editionUrl/$studentId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion succeeds when student with given id exists`() {
        every { studentService.addStudentStatusSuggestion(studentId, any(), testEdition) } just Runs
        mockMvc.perform(
            post("$editionUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(defaultPrincipal)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `addStudentStatusSuggestion returns 404 Not Found if student with given id does not exist`() {
        every { studentService.addStudentStatusSuggestion(studentId, any(), testEdition) }
            .throws(InvalidStudentIdException())
        mockMvc.perform(
            post("$editionUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(defaultPrincipal)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion returns 403 Forbidden if coach already made suggestion for student`() {
        every { studentService.addStudentStatusSuggestion(studentId, any(), testEdition) }
            .throws(ForbiddenOperationException())
        mockMvc.perform(
            post("$editionUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(defaultPrincipal)
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `addStudentStatusSuggestion returns 404 Not Found if coach doesn't exist`() {
        every { studentService.addStudentStatusSuggestion(studentId, any(), testEdition) }
            .throws(InvalidUserIdException())

        mockMvc.perform(
            post("$editionUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(defaultPrincipal)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion returns 401 if we try creating a suggestion on behalf of another user`() {
        every { userDetailService.getUserFromPrincipal(any()) } returns
            User("other user", "email", Role.Coach, "password")

        mockMvc.perform(
            post("$editionUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(defaultPrincipal)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `deleteStudentStatusSuggestion succeeds when student, suggestion and coach exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId, testEdition) } just Runs

        mockMvc.perform(
            delete("$editionUrl/$studentId/suggestions/$coachId")
                .principal(defaultPrincipal)
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 404 Not Found if student doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId, testEdition) }
            .throws(InvalidStudentIdException())

        mockMvc.perform(
            delete("$editionUrl/$studentId/suggestions/$coachId")
                .principal(defaultPrincipal)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 400 Bad Request if suggestion doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId, testEdition) }
            .throws(FailedOperationException())

        mockMvc.perform(
            delete("$editionUrl/$studentId/suggestions/$coachId")
                .principal(defaultPrincipal)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 404 Not Found if coach doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId, testEdition) }
            .throws(InvalidUserIdException())

        mockMvc.perform(
            delete("$editionUrl/$studentId/suggestions/$coachId")
                .principal(defaultPrincipal)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 401 if we try deleting a suggestion made by another user`() {
        every { userDetailService.getUserFromPrincipal(any()) } returns
            User("other user", "email", Role.Coach, "password")

        mockMvc.perform(
            delete("$editionUrl/$studentId/suggestions/$coachId")
                .principal(defaultPrincipal)
        ).andExpect(status().isUnauthorized)
    }
}
