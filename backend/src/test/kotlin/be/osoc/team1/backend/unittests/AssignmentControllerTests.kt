package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.AssignmentController
import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.AssignmentService
import be.osoc.team1.backend.util.PositionSerializer
import be.osoc.team1.backend.util.StudentSerializer
import be.osoc.team1.backend.util.UserSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

@UnsecuredWebMvcTest(AssignmentController::class)
class AssignmentControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var assignmentService: AssignmentService

    private val testId = UUID.randomUUID()
    private val testStudent = Student("Jitse", "Willaert", "testEdition")
    private val testSkill = Skill("Test")
    private val testPosition = Position(testSkill, 2)
    private val testSuggester = User("Jitse", "Willaert", Role.Admin, "Test")
    private val testAssignment = Assignment(testStudent, testPosition, testSuggester, "reason")
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun beforeEach() {
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        val simpleModule = SimpleModule()
        simpleModule.addSerializer(Position::class.java, PositionSerializer())
        simpleModule.addSerializer(Student::class.java, StudentSerializer())
        simpleModule.addSerializer(User::class.java, UserSerializer())
        objectMapper.registerModule(simpleModule)
    }

    @Test
    fun `getAssignmentById returns assignment if assignment with given id exists`() {
        val jsonRepresentation = objectMapper.writeValueAsString(testAssignment)
        every { assignmentService.getById(testId) } returns testAssignment
        mockMvc.perform(get("/assignments/$testId")).andExpect(status().isOk)
            .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getAssignmentById returns 404 Not Found if assignment with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { assignmentService.getById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("/assignments/$differentId"))
            .andExpect(status().isNotFound)
    }
}
