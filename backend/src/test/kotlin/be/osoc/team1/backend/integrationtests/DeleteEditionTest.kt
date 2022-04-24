package be.osoc.team1.backend.integrationtests

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.SuggestionEnum
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.repositories.EditionRepository
import be.osoc.team1.backend.repositories.PositionRepository
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.repositories.StatusSuggestionRepository
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeleteEditionTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var editionRepository: EditionRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var assignmentRepository: AssignmentRepository

    @Autowired
    private lateinit var positionRepository: PositionRepository

    @Autowired
    private lateinit var statusSuggestionRepository: StatusSuggestionRepository

    @Test
    fun `deleteEdition removes all entities from the edition`() {
        val editionName = "testEdition"
        val edition = Edition(editionName, true)
        editionRepository.save(edition)
        val coach = User("", "", Role.Coach, "")
        userRepository.save(coach)
        val student = Student("", "", editionName)
        val suggestion = StatusSuggestion(coach.id, SuggestionEnum.Yes, "", editionName)
        student.statusSuggestions.add(suggestion)
        studentRepository.save(student)
        val skill = Skill("")
        val position = Position(skill, 1, editionName)
        val assignment = Assignment(student, position, coach, "", editionName)
        val project = Project(
            "", "", "", editionName,
            mutableListOf(coach), listOf(position), mutableListOf(assignment)
        )
        projectRepository.save(project)

        val differentEditionName = "differentEdition"
        val differentEdition = Edition(differentEditionName, false)
        editionRepository.save(differentEdition)
        val differentEditionStudent = Student("", "", differentEditionName)
        val differentEditionSuggestion = StatusSuggestion(coach.id, SuggestionEnum.Yes, "", differentEditionName)
        differentEditionStudent.statusSuggestions.add(differentEditionSuggestion)
        studentRepository.save(differentEditionStudent)
        val differentEditionPosition = Position(skill, 1, differentEditionName)
        val differentEditionAssignment = Assignment(differentEditionStudent, differentEditionPosition, coach, "", differentEditionName)
        val differentEditionProject = Project(
            "", "", "", differentEditionName,
            mutableListOf(coach), listOf(differentEditionPosition), mutableListOf(differentEditionAssignment)
        )
        projectRepository.save(project)

        assertEquals(listOf(student, differentEditionStudent), studentRepository.findAll())
        assertEquals(listOf(project, differentEditionProject), projectRepository.findAll())
        assertEquals(listOf(assignment, differentEditionAssignment), assignmentRepository.findAll())
        assertEquals(listOf(position, differentEditionPosition), positionRepository.findAll())
        assertEquals(listOf(suggestion, differentEditionSuggestion), statusSuggestionRepository.findAll())
        restTemplate.exchange(URI("/editions/$editionName"), HttpMethod.DELETE, HttpEntity("", HttpHeaders()), String::class.java)
        assertEquals(listOf(differentEditionStudent), studentRepository.findAll())
        assertEquals(listOf(differentEditionProject), projectRepository.findAll())
        assertEquals(listOf(differentEditionAssignment), assignmentRepository.findAll())
        assertEquals(listOf(differentEditionPosition), positionRepository.findAll())
        assertEquals(listOf(differentEditionSuggestion), statusSuggestionRepository.findAll())
    }
}
