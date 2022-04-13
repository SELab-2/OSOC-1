package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.*
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.StudentService
import be.osoc.team1.backend.services.UserService
import io.mockk.InternalPlatformDsl.toArray
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JsonContent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*
import org.springframework.boot.test.json.JacksonTester

import org.springframework.beans.factory.annotation.Autowired




@JsonTest
class SerializationTests {

    @Autowired
    private val jacksonTester: JacksonTester<Project>? = null

    @BeforeEach
    fun setMockRequest() {
        val mockRequest = MockHttpServletRequest()
        mockRequest.setScheme("https")
        mockRequest.setServerName("example.com")
        mockRequest.setServerPort(-1)
        mockRequest.setContextPath("/api")
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(mockRequest))
    }

    @Test
    fun `Serialization of Project returns the correct result`() {
        val testStudent = Student("Jitse", "Willaert")
        val testPosition = Position(Skill("backend"), 2)
        val testUser = User("suggester", "email", Role.Coach, "password")
        val testProject = Project(
                "Test", "Client", "a test project",
                assignments = mutableListOf(
                        Assignment(testStudent, testPosition, testUser, "reason")
                ),
                coaches = mutableListOf(testUser)
        )
        val json: JsonContent<Project> = jacksonTester!!.write(testProject)

        assertThat(json).extractingJsonPathValue("$.coaches").isEqualTo(mutableListOf("https://example.com/api/users/" + testUser.id))
        assertThat(json).extractingJsonPathValue("$.assignments[0].student").isEqualTo("https://example.com/api/students/" + testStudent.id)
        assertThat(json).extractingJsonPathValue("$.assignments[0].suggester").isEqualTo("https://example.com/api/users/" + testUser.id)
    }
}
