package be.osoc.team1.backend.integrationtests

import be.osoc.team1.backend.controllers.StudentController
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.services.StudentService
import org.junit.ClassRule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

// All tests in this class will use the same database
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
class StudentTests {

    // You should clean the repository after every test
    @AfterEach
    fun cleanup() {
        testStudentRepository.deleteAll()
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
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var studentController: StudentController

    private val testStudent = Student("Tom", "Alard")

    // This is an example test just to show all autowired vars will connect to the same database
    @Test
    fun `All autowired vars are connected to the same empty database`() {
        studentController.addStudent(testStudent)
        studentService.addStudent(testStudent)
        testStudentRepository.save(testStudent).id

        assert(testStudentRepository.findAll().count() == 3)
    }

    @Test
    fun `addStudent should add and return correct UUID for getStudent to get original`() {
        val loclist = studentController.addStudent(testStudent).headers[HttpHeaders.LOCATION]?.get(0)!!.split('/')
        val loc: UUID = UUID.fromString(loclist[loclist.size - 1])
        assert(studentController.getStudentById(loc).firstName.equals(testStudent.firstName))
        assert(studentController.getStudentById(loc).lastName.equals(testStudent.lastName))
    }

    @Test
    fun `getStudent after deleteStudent should throw InvalidIdException`() {
        val loclist = studentController.addStudent(testStudent).headers[HttpHeaders.LOCATION]?.get(0)!!.split('/')
        val loc: UUID = UUID.fromString(loclist[loclist.size - 1])
        studentController.deleteStudentById(loc)
        assertThrows<InvalidIdException> { studentController.getStudentById(loc) }
    }
}
