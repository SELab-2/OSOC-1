package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.CommunicationTypeEnum
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.SuggestionEnum
import be.osoc.team1.backend.entities.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.json.JsonContent
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

@JsonTest
class SerializationTests {

    @Autowired
    private val projectJacksonTester: JacksonTester<Project>? = null

    @Autowired
    private val studentJacksonTester: JacksonTester<Student>? = null

    @Autowired
    private val assignmentJacksonTester: JacksonTester<Assignment>? = null

    private val testEdition = "testEdition"

    @BeforeEach
    fun setMockRequest() {
        val mockRequest = MockHttpServletRequest()
        mockRequest.scheme = "https"
        mockRequest.serverName = "example.com"
        mockRequest.serverPort = -1
        mockRequest.contextPath = "/api"
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(mockRequest))
    }

    @Test
    fun `Serialization of Project returns the correct result`() {
        val testStudent = Student("Jitse", "Willaert", testEdition)
        val testPosition = Position(Skill("backend"), 2, testEdition)
        val testUser = User("suggester", "email", Role.Coach, "password")
        val testAssignment = Assignment(testStudent, testPosition, testUser, "reason", testEdition)
        val testProject = Project(
            "Test", "Client", "a test project", testEdition,
            coaches = mutableListOf(testUser),
            positions = mutableListOf(testPosition),
            assignments = mutableListOf(testAssignment)
        )
        val json: JsonContent<Project> = projectJacksonTester!!.write(testProject)

        assertThat(json).extractingJsonPathValue("$.coaches")
            .isEqualTo(mutableListOf("https://example.com/api/users/${testUser.id}"))
        assertThat(json).extractingJsonPathValue("$.positions")
            .isEqualTo(mutableListOf("https://example.com/api/positions/${testPosition.id}"))
        assertThat(json).extractingJsonPathValue("$.assignments")
            .isEqualTo(mutableListOf("https://example.com/api/assignments/${testAssignment.id}"))
    }

    @Test
    fun `Serialization of Project returns the correct result when it's empty`() {
        val testProject = Project("Test", "Client", "a test project", testEdition)
        val json: JsonContent<Project> = projectJacksonTester!!.write(testProject)

        assertThat(json).extractingJsonPathValue("$.coaches").isEqualTo(mutableListOf<String>())
        assertThat(json).extractingJsonPathValue("$.positions").isEqualTo(mutableListOf<String>())
        assertThat(json).extractingJsonPathValue("$.assignments").isEqualTo(mutableListOf<String>())
    }

    @Test
    fun `Serialization of Student returns the correct result`() {
        val testStatusSuggestion = StatusSuggestion(UUID.randomUUID(), SuggestionEnum.Yes, "motivation", testEdition)
        val testCommunication = Communication("test", CommunicationTypeEnum.Email, testEdition)
        val testStudent = Student("Jitse", "Willaert", testEdition)
        testStudent.communications.add(testCommunication)
        testStudent.statusSuggestions.add(testStatusSuggestion)
        val json: JsonContent<Student> = studentJacksonTester!!.write(testStudent)

        assertThat(json).extractingJsonPathValue("$.statusSuggestions")
            .isEqualTo(mutableListOf("https://example.com/api/statusSuggestions/${testStatusSuggestion.id}"))
        assertThat(json).extractingJsonPathValue("$.communications")
            .isEqualTo(mutableListOf("https://example.com/api/$testEdition/communications/${testCommunication.id}"))
    }

    @Test
    fun `Serialization of Student returns the correct result when it's empty`() {
        val testStudent = Student("Jitse", "Willaert", testEdition)
        val json: JsonContent<Student> = studentJacksonTester!!.write(testStudent)

        assertThat(json).extractingJsonPathValue("$.statusSuggestions").isEqualTo(mutableListOf<String>())
        assertThat(json).extractingJsonPathValue("$.communications").isEqualTo(mutableListOf<String>())
    }

    @Test
    fun `Serialization of Assignment returns the correct result`() {
        val testStudent = Student("Jitse", "Willaert", testEdition)
        val testSkill = Skill("Test")
        val testPosition = Position(testSkill, 2, testEdition)
        val testSuggester = User("Jitse", "Willaert", Role.Admin, "Test")
        val testAssignment = Assignment(testStudent, testPosition, testSuggester, "reason", testEdition)
        val json: JsonContent<Assignment> = assignmentJacksonTester!!.write(testAssignment)

        assertThat(json).extractingJsonPathValue("$.student")
            .isEqualTo("https://example.com/api/$testEdition/students/${testStudent.id}")
        assertThat(json).extractingJsonPathValue("$.position")
            .isEqualTo("https://example.com/api/positions/${testPosition.id}")
        assertThat(json).extractingJsonPathValue("$.suggester")
            .isEqualTo("https://example.com/api/users/${testSuggester.id}")
    }
}
