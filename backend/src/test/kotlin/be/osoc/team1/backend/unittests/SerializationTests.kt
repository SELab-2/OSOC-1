package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.Student
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

@JsonTest
class SerializationTests {

    @Autowired
    private val projectJacksonTester: JacksonTester<Project>? = null

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
        val testPosition = Position(Skill("backend"), 2)
        val testUser = User("suggester", "email", Role.Coach, "password")
        val testAssignment = Assignment(testStudent, testPosition, testUser, "reason")
        val testProject = Project(
            "Test", "Client", "a test project", testEdition,
            coaches = mutableListOf(testUser),
            positions = mutableListOf(testPosition),
            assignments = mutableListOf(testAssignment)
        )
        val json: JsonContent<Project> = projectJacksonTester!!.write(testProject)

        assertThat(json).extractingJsonPathValue("$.coaches")
            .isEqualTo(mutableListOf("https://example.com/api/users/${testUser.id}"))
        assertThat(json).extractingJsonPathValue("$.assignments")
            .isEqualTo(mutableListOf("https://example.com/api/$testEdition/assignments/${testAssignment.id}"))
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
    fun `Serialization of Assignment returns the correct result`() {
        val testStudent = Student("Jitse", "Willaert", testEdition)
        val testSkill = Skill("Test")
        val testPosition = Position(testSkill, 2)
        val testSuggester = User("Jitse", "Willaert", Role.Admin, "Test")
        val testAssignment = Assignment(testStudent, testPosition, testSuggester, "reason")
        val json: JsonContent<Assignment> = assignmentJacksonTester!!.write(testAssignment)

        assertThat(json).extractingJsonPathValue("$.student")
            .isEqualTo("https://example.com/api/$testEdition/students/${testStudent.id}")
        assertThat(json).extractingJsonPathValue("$.suggester")
            .isEqualTo("https://example.com/api/users/${testSuggester.id}")
    }
}
