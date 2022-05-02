package be.osoc.team1.backend.integrationtests

import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.repositories.StudentRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EntitySaveTests {
    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @AfterEach
    fun cleanup() {
        studentRepository.deleteAll()
        projectRepository.deleteAll()
    }

    @Test
    fun `test if saving a student with a new skill saves the skill first`() {
        val student = Student("firstname", "lastname", skills = sortedSetOf(Skill("test")))
        studentRepository.save(student)
    }

    @Test
    fun `test if deleting a student with a shared skill doesn't cause issues`() {
        val skill = Skill("test")
        val student1 = Student("firstname", "lastname", skills = sortedSetOf(skill))
        val student2 = Student("firstname", "lastname", skills = sortedSetOf(skill))
        studentRepository.save(student1)
        studentRepository.save(student2)
        studentRepository.delete(student1)
    }

    @Test
    fun `test if saving a project with a new skill saves the new skill first`() {
        val project = Project("name", "clientName", "description",
            positions = listOf(Position(Skill("test"), 2))
        )
        projectRepository.save(project)
    }

    @Test
    fun `test if deleting a project with a shared skill doesn't cause issues`() {
        val skill = Skill("test")
        val project1 = Project("name", "clientName", "description",
            positions = listOf(Position(skill, 2))
        )
        val project2 = Project("name", "clientName", "description",
            positions = listOf(Position(skill, 2))
        )
        projectRepository.save(project1)
        projectRepository.save(project2)
        projectRepository.delete(project1)
    }
}
