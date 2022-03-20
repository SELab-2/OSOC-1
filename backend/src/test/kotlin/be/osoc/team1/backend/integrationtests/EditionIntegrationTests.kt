package be.osoc.team1.backend.integrationtests

import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.StudentService
import be.osoc.team1.backend.services.UserService
import org.junit.ClassRule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

// All tests in this class will use the same database
// Boilerplate code is copy pasted from the integration-test-example branch (thanks Michael!)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
class EditionIntegrationTests {

    // Add some test data
    @BeforeEach
    fun populate() {
        testStudentRepository.deleteAll()
        testProjectRepository.deleteAll()
        testUserRepository.deleteAll()

        testStudentRepository.save(Student("Tom", "Alard"))
        testStudentRepository.save(Student("Maarten", "Stevens"))
        testProjectRepository.save(Project("Rubik's Cube solver in Prolog", ""))
        testProjectRepository.save(Project("Equidistant letter sequences", ""))
        testUserRepository.save(User("coach", "", Role.Coach, ""))
        testUserRepository.save(User("disabled", "", Role.Disabled, ""))
        testUserRepository.save(User("Miet", "", Role.Admin, ""))
    }

    companion object {
        @ClassRule
        @Container
        private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" } // VERY IMPORTANT!
        }
    }

    @Autowired
    private lateinit var testStudentRepository: StudentRepository
    @Autowired
    private lateinit var testProjectRepository: ProjectRepository
    @Autowired
    private lateinit var testUserRepository: UserRepository

    @Autowired
    private lateinit var studentService: StudentService
    @Autowired
    private lateinit var projectService: ProjectService
    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var editionService: EditionService

    private fun databaseWasCorrectlyFiltered(): Boolean {
        // IntelliJ is lying here, there is no isEmpty for Iterators :(
        val noStudents = studentService.getAllStudents().count() == 0
        val noProjects = projectService.getAllProjects().count() == 0
        val onlyAdminUserRemaining =
            userService.getAllUsers().count() == 1 &&
                userService.getAllUsers().first().role == Role.Admin
        return noStudents && noProjects && onlyAdminUserRemaining
    }

    @Test
    fun `createNewEdition deletes all students, projects, coaches and disabled users from database`() {
        editionService.createNewEdition()
        assert(databaseWasCorrectlyFiltered())
    }

    @Test
    fun `createNewEdition is idempotent`() {
        // Should work for any integer greater than zero
        repeat(10) {
            editionService.createNewEdition()
            assert(databaseWasCorrectlyFiltered())
        }
    }
}
