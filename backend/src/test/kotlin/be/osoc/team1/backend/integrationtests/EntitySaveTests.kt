package be.osoc.team1.backend.integrationtests

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.repositories.UserRepository
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

    @Autowired
    lateinit var userRepository: UserRepository

    @AfterEach
    fun cleanup() {
        projectRepository.deleteAll()
        userRepository.deleteAll()
        studentRepository.deleteAll()
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
        val position = Position(Skill("test"), 2)
        val student = Student("firstname", "lastname")
        val user = User("username", "email", Role.Coach, "password")
        studentRepository.save(student)
        userRepository.save(user)

        val project = Project(
            "name", "clientName", "description",
                positions = listOf(position)
        )
        /*
         * We save the project before assigning students, this is how it is normally done in the application and it is
         * a requirement because the Position objects need to exist in the database before attempting to save the
         * assignments.
         */
        projectRepository.save(project)
        project.assignments.add(Assignment(
            student,
            position,
            user,
            "reason"
        ))
        projectRepository.save(project)
    }

    @Test
    fun `test if deleting a project with a shared skill doesn't cause issues`() {
        val student = Student("firstname", "lastname")
        val user = User("username", "email", Role.Coach, "password")
        studentRepository.save(student)
        userRepository.save(user)

        val skill = Skill("test")
        val position1 = Position(skill, 2)
        val project1 = Project(
            "name", "clientName", "description",
            positions = listOf(position1),
        )
        projectRepository.save(project1)
        project1.assignments.add(
            Assignment(
                student,
                position1,
                user,
                "reason"
            )
        )
        projectRepository.save(project1)

        val position2 = Position(skill, 2)
        val project2 = Project(
            "name", "clientName", "description",
            positions = listOf(position2),
        )
        projectRepository.save(project2)
        project2.assignments.add(
            Assignment(
                student,
                position2,
                user,
                "reason"
            )
        )
        projectRepository.save(project2)
        projectRepository.delete(project1)
    }
}
