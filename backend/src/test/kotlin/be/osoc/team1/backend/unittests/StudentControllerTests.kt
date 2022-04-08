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
    private val testOrganization = "testOrganization"
    private val testEditionName = "testEditionName"
    private val testStudent = Student("Tom", "Alard", testOrganization, testEditionName)
    private val objectMapper = ObjectMapper()
    private val jsonRepresentation = objectMapper.writeValueAsString(testStudent)
    private val baseUrl = "/$testOrganization/$testEditionName/students"
    private val defaultStatusFilter =
        listOf(StatusEnum.Yes, StatusEnum.No, StatusEnum.Maybe, StatusEnum.Undecided)
    private val testSuggestion = StatusSuggestion(coachId, SuggestionEnum.Yes, "test motivation")
    private val token = TestingAuthenticationToken(null, null)

    @BeforeEach
    fun beforeEach() {
        every { userDetailService.getUserFromPrincipal(any()) } returns testCoach
    }

    @Test
    fun `getAllStudents should not fail`() {
        every {
            studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "", true, testOrganization, testEditionName, testCoach)
        } returns emptyList()
        mockMvc.perform(get(baseUrl)
            .principal(token))
            .andExpect(status().isOk)
    }

    @Test
    fun `getAllStudents paging returns the correct amount`() {
        val testList = listOf(testStudent)
        every {
            studentService.getAllStudents(0, 1, "id", defaultStatusFilter, "", true, testOrganization, testEditionName, testCoach)
        } returns testList
        mockMvc.perform(get("$baseUrl?pageNumber=0&pageSize=1")
            .principal(token))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList)))
    }

    @Test
    fun `getAllStudents status filtering parses the correct statuses`() {
        val testStudent1 = Student("L1", "VC", testOrganization, testEditionName)
        testStudent1.status = StatusEnum.Yes
        val testStudent2 = Student("L2", "VC", testOrganization, testEditionName)
        testStudent2.status = StatusEnum.No
        val testStudent3 = Student("L3", "VC", testOrganization, testEditionName)
        testStudent3.status = StatusEnum.Maybe
        val testStudent4 = Student("L4", "VC", testOrganization, testEditionName)
        testStudent4.status = StatusEnum.Undecided
        val allStudents = listOf(testStudent1, testStudent2, testStudent3, testStudent4)
        every {
            studentService.getAllStudents(0, 50, "id", listOf(StatusEnum.Yes), "", true, testOrganization, testEditionName, testCoach)
        } returns listOf(testStudent1)
        every {
            studentService.getAllStudents(0, 50, "id", listOf(StatusEnum.No), "", true, testOrganization, testEditionName, testCoach)
        } returns listOf(testStudent2)
        every {
            studentService.getAllStudents(0, 50, "id", listOf(StatusEnum.Maybe), "", true, testOrganization, testEditionName, testCoach)
        } returns listOf(testStudent3)
        every {
            studentService.getAllStudents(0, 50, "id", listOf(StatusEnum.Undecided), "", true, testOrganization, testEditionName, testCoach)
        } returns listOf(testStudent4)
        every {
            studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "", true, testOrganization, testEditionName, testCoach)
        } returns allStudents
        mockMvc.perform(get("$baseUrl?status=Yes").principal(token))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(listOf(testStudent1))))
        mockMvc.perform(get("$baseUrl?status=No").principal(token))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(listOf(testStudent2))))
        mockMvc.perform(get("$baseUrl?status=Maybe").principal(token))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(listOf(testStudent3))))
        mockMvc.perform(get("$baseUrl?status=Undecided").principal(token))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(listOf(testStudent4))))
        mockMvc.perform(get("$baseUrl?status=Yes,No,Maybe,Undecided").principal(token))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(allStudents)))
    }

    @Test
    fun `getAllStudents name filtering parses the correct name`() {
        val testList = listOf(Student("_", "_", testOrganization, testEditionName))
        val testList2 = listOf(Student("_2", "_2", testOrganization, testEditionName))
        every {
            studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "lars", true, testOrganization, testEditionName, testCoach)
        } returns testList
        every {
            studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "lars test", true, testOrganization, testEditionName, testCoach)
        } returns testList2
        // tests the url parsing + with url encoding
        mockMvc.perform(get("$baseUrl?name=lars").principal(token))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList)))
        mockMvc.perform(get("$baseUrl?name=lars%20test").principal(token))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList2)))
    }

    @Test
    fun `getAllStudents include filtering parses the boolean correctly`() {
        val testList = listOf(Student("test", "testie", testOrganization, testEditionName))
        every { studentService.getAllStudents(0, 50, "id", defaultStatusFilter, "", false, testOrganization, testEditionName, testCoach) } returns
            testList
        mockMvc.perform(get("$baseUrl?includeSuggested=false").principal(token))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList)))
    }

    @Test
    fun `getStudentById returns student if student with given id exists`() {
        every { studentService.getStudentById(studentId) } returns testStudent
        mockMvc.perform(get("$baseUrl/$studentId"))
            .andExpect(status().isOk)
            .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getStudentById returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.getStudentById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("$baseUrl/$differentId")).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentById succeeds if student with given id exists`() {
        every { studentService.deleteStudentById(studentId) } just Runs
        mockMvc.perform(delete("$baseUrl/$studentId")).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteStudentById returns 404 Not Found if student with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { studentService.deleteStudentById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(delete("$baseUrl/$differentId")).andExpect(status().isNotFound)
    }

    @Test
    fun `addStudent should return created student`() {
        every { studentService.addStudent(any()) } returns testStudent
        val mvcResult =
            mockMvc.perform(
                post(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRepresentation)
            )
                .andExpect(status().isCreated)
                .andReturn()
        val locationHeader = mvcResult.response.getHeader("Location")
        assert(locationHeader!!.endsWith("$baseUrl/${testStudent.id}"))
    }

    @Test
    fun `setStudentStatus succeeds when student with given id exists`() {
        val status = StatusEnum.Yes
        every { studentService.setStudentStatus(studentId, status) } just Runs
        mockMvc.perform(
            post("$baseUrl/$studentId/status")
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
            post("$baseUrl/$studentId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion succeeds when student with given id exists`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) } just Runs
        mockMvc.perform(
            post("$baseUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(token)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `addStudentStatusSuggestion returns 404 Not Found if student with given id does not exist`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) }
            .throws(InvalidStudentIdException())
        mockMvc.perform(
            post("$baseUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(token)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion returns 403 Forbidden if coach already made suggestion for student`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) }
            .throws(ForbiddenOperationException())
        mockMvc.perform(
            post("$baseUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(token)
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `addStudentStatusSuggestion returns 404 Not Found if coach doesn't exist`() {
        every { studentService.addStudentStatusSuggestion(studentId, any()) }
            .throws(InvalidUserIdException())

        mockMvc.perform(
            post("$baseUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(token)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `addStudentStatusSuggestion returns 401 if we try creating a suggestion on behalf of another user`() {
        every { userDetailService.getUserFromPrincipal(any()) } returns
            User("other user", "email", Role.Coach, "password")

        mockMvc.perform(
            post("$baseUrl/$studentId/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSuggestion))
                .principal(token)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `deleteStudentStatusSuggestion succeeds when student, suggestion and coach exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) } just Runs

        mockMvc.perform(delete("$baseUrl/$studentId/suggestions/$coachId")
            .principal(token)
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 404 Not Found if student doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) }
            .throws(InvalidStudentIdException())

        mockMvc.perform(delete("$baseUrl/$studentId/suggestions/$coachId")
            .principal(token)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 400 Bad Request if suggestion doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) }
            .throws(FailedOperationException())

        mockMvc.perform(delete("$baseUrl/$studentId/suggestions/$coachId")
            .principal(token)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 404 Not Found if coach doesn't exist`() {
        every { studentService.deleteStudentStatusSuggestion(studentId, coachId) }
            .throws(InvalidUserIdException())

        mockMvc.perform(delete("$baseUrl/$studentId/suggestions/$coachId")
            .principal(token)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentStatusSuggestion returns 401 if we try deleting a suggestion made by another user`() {
        every { userDetailService.getUserFromPrincipal(any()) } returns
            User("other user", "email", Role.Coach, "password")

        mockMvc.perform(delete("$baseUrl/$studentId/suggestions/$coachId")
            .principal(token)
        ).andExpect(status().isUnauthorized)
    }
}
