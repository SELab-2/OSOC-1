package be.osoc.team1.backend.integrationtests

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
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.repositories.PositionRepository
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.repositories.StatusSuggestionRepository
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.StudentService
import be.osoc.team1.backend.services.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeleteEditionTest {

    @Autowired
    private lateinit var editionService: EditionService

    @Autowired
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var projectService: ProjectService

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
        editionService.makeEditionActive(editionName)
        val coach = User("", "", Role.Coach, "")
        userService.registerUser(coach)
        val student = Student("", "", editionName)
        studentService.addStudent(student)
        val suggestion = StatusSuggestion(coach.id, SuggestionEnum.Yes, "", editionName)
        studentService.addStudentStatusSuggestion(student.id, suggestion, editionName)
        val communication = Communication("", CommunicationTypeEnum.Email, editionName)
        studentService.addCommunicationToStudent(student.id, communication, editionName)
        val skill = Skill("")
        val position = Position(skill, 1, editionName)
        val assignment = Assignment(student, position, coach, "", editionName)
        val project = Project(
            "", "", "", editionName,
            mutableListOf(coach), listOf(position), mutableListOf(assignment)
        )
        projectService.postProject(project)

        val differentEditionName = "differentEdition"
        editionService.makeEditionInactive(differentEditionName)
        val differentEditionStudent = Student("", "", differentEditionName)
        studentService.addStudent(differentEditionStudent)
        val differentEditionSuggestion = StatusSuggestion(coach.id, SuggestionEnum.Yes, "", differentEditionName)
        studentService.addStudentStatusSuggestion(differentEditionStudent.id, differentEditionSuggestion, differentEditionName)
        val differentEditionPosition = Position(skill, 1, differentEditionName)
        val differentEditionAssignment = Assignment(differentEditionStudent, differentEditionPosition, coach, "", differentEditionName)
        val differentEditionProject = Project(
            "", "", "", differentEditionName,
            mutableListOf(coach), listOf(differentEditionPosition), mutableListOf(differentEditionAssignment)
        )
        projectService.postProject(differentEditionProject)

        assertEquals(listOf(student, differentEditionStudent), studentRepository.findAll())
        assertEquals(listOf(project, differentEditionProject), projectRepository.findAll())
        assertEquals(listOf(assignment, differentEditionAssignment), assignmentRepository.findAll())
        assertEquals(listOf(position, differentEditionPosition), positionRepository.findAll())
        assertEquals(listOf(suggestion, differentEditionSuggestion), statusSuggestionRepository.findAll())
        editionService.deleteEdition(editionName)
        assertEquals(listOf(differentEditionStudent), studentRepository.findAll())
        assertEquals(listOf(differentEditionProject), projectRepository.findAll())
        assertEquals(listOf(differentEditionAssignment), assignmentRepository.findAll())
        assertEquals(listOf(differentEditionPosition), positionRepository.findAll())
        assertEquals(listOf(differentEditionSuggestion), statusSuggestionRepository.findAll())
    }
}