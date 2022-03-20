package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.StudentService
import be.osoc.team1.backend.services.UserService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class EditionServiceTests {

    private val relaxedStudentService: StudentService = mockk(relaxed = true)
    private val relaxedProjectService: ProjectService = mockk(relaxed = true)
    private val relaxedUserService: UserService = mockk(relaxed = true)

    @Test
    fun `createNewEdition calls deleteStudentById on all students in the database`() {
        val testStudent1 = Student("Tom", "Alard")
        val testStudent2 = Student("Maarten", "Stevens")
        val studentService: StudentService = mockk()
        every { studentService.getAllStudents() } returns listOf(testStudent1, testStudent2)
        every { studentService.deleteStudentById(any()) } just Runs
        val editionService = EditionService(studentService, relaxedProjectService, relaxedUserService)
        editionService.createNewEdition()
        verify { studentService.deleteStudentById(testStudent1.id) }
        verify { studentService.deleteStudentById(testStudent2.id) }
    }

    @Test
    fun `createNewEdition calls deleteProjectById on all projects in the database`() {
        val testProject1 = Project("Rubik's Cube solver in Prolog", "")
        val testProject2 = Project("Equidistant letter sequences", "")
        val projectService: ProjectService = mockk()
        every { projectService.getAllProjects() } returns listOf(testProject1, testProject2)
        every { projectService.deleteProjectById(any()) } just Runs
        val editionService = EditionService(relaxedStudentService, projectService, relaxedUserService)
        editionService.createNewEdition()
        verify { projectService.deleteProjectById(testProject1.id) }
        verify { projectService.deleteProjectById(testProject2.id) }
    }

    @Test
    fun `createNewEdition calls deleteUserById on all coaches and disabled users in the database`() {
        val testCoach = User("coach", "", Role.Coach, "")
        val testDisabled = User("disabled", "", Role.Disabled, "")
        val testAdmin = User("Miet", "", Role.Admin, "")
        val userService: UserService = mockk()
        every { userService.getAllUsers() } returns listOf(testCoach, testDisabled, testAdmin)
        every { userService.deleteUserById(any()) } just Runs
        val editionService = EditionService(relaxedStudentService, relaxedProjectService, userService)
        editionService.createNewEdition()
        verify { userService.deleteUserById(testCoach.id) }
        verify { userService.deleteUserById(testDisabled.id) }
        verify(exactly = 0) { userService.deleteUserById(testAdmin.id) }
    }
}
