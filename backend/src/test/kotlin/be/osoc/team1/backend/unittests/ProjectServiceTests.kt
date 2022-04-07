package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.UserService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class ProjectServiceTests {
    private val testId = UUID.randomUUID()
    private val testStudent = Student("Lars", "Van Cauter", "", "")
    private val testCoach = User("Lars2 Van Cauter", "lars2@email.com", Role.Coach, "password")
    private val testProject =
        Project("Test", "a test project", mutableListOf(testStudent), mutableListOf(testCoach))
    private val savedProject =
        Project(
            "Saved",
            "a saved project",
            mutableListOf(testStudent),
            mutableListOf(testCoach)
        )

    private fun getRepository(projectAlreadyExists: Boolean): ProjectRepository {
        val repository: ProjectRepository = mockk()
        every { repository.existsById(any()) } returns projectAlreadyExists
        every { repository.findByIdOrNull(any()) } returns
            if (projectAlreadyExists) testProject else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns savedProject
        every { repository.findAll() } returns listOf(testProject)
        return repository
    }

    private fun getUserService(): UserService {
        val userService: UserService = mockk()
        every { userService.getUserById(testCoach.id) } returns testCoach
        return userService
    }

    @Test
    fun `getAllProjects does not fail`() {
        val service = ProjectService(getRepository(true), getUserService())
        assertEquals(listOf(testProject), service.getAllProjects(""))
    }

    @Test
    fun `getAllProjects name filtering returns only projects with those names`() {
        val testProject = Project("Lars", "Cauter")
        val testProject2 = Project("Sral", "Retuac")
        val testProject3 = Project("Arsl", "Auterc")
        val testProject4 = Project("Rsla", "Uterca")
        val repository: ProjectRepository = mockk()
        val allProjects = listOf(testProject, testProject2, testProject3, testProject4)
        every { repository.findAll() } returns allProjects
        val service = ProjectService(repository, getUserService())
        assertEquals(listOf(testProject), service.getAllProjects("lars"))
        assertEquals(listOf(testProject, testProject3), service.getAllProjects("ars"))
        assertEquals(listOf<Project>(), service.getAllProjects("uter"))
        assertEquals(allProjects, service.getAllProjects(""))
    }

    @Test
    fun `getProjectById succeeds when project with id exists`() {
        val service = ProjectService(getRepository(true), getUserService())
        assertEquals(testProject, service.getProjectById(testId))
    }

    @Test
    fun `getProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false), getUserService())
        assertThrows<InvalidProjectIdException> { service.getProjectById(testId) }
    }

    @Test
    fun `deleteProjectById succeeds when project with id exists`() {
        val repo = getRepository(true)
        val service = ProjectService(repo, getUserService())
        service.deleteProjectById(testId)
        verify { repo.deleteById(testId) }
    }

    @Test
    fun `deleteProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false), getUserService())
        assertThrows<InvalidProjectIdException> { service.deleteProjectById(testId) }
    }

    @Test
    fun `postProject returns some other id than what was passed`() {
        val service = ProjectService(getRepository(false), getUserService())
        assertNotEquals(service.postProject(testProject).id, testId)
    }

    @Test
    fun `patchProject updates project when project with same id exists`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getUserService())
        service.patchProject(testProject)
        verify { repository.save(testProject) }
    }

    @Test
    fun `patchProject fails when no project with same id exists`() {
        val service = ProjectService(getRepository(false), getUserService())
        assertThrows<InvalidProjectIdException> { service.patchProject(testProject) }
    }

    @Test
    fun `addStudentToProject runs`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getUserService())
        val student = Student("Lars", "Van Cauter", "", "")
        service.addStudentToProject(testProject.id, student)
        verify { repository.save(testProject) }
    }

    @Test
    fun `addStudentToProject fails when project doesnt exist`() {
        val service = ProjectService(getRepository(false), getUserService())
        val student = Student("Lars", "Van Cauter", "", "")
        assertThrows<InvalidProjectIdException> {
            service.addStudentToProject(testProject.id, student)
        }
    }

    @Test
    fun `addCoachToProject runs`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getUserService())
        service.addCoachToProject(testProject.id, testCoach.id)
        verify { repository.save(testProject) }
    }

    @Test
    fun `addCoachToProject fails when project doesnt exit`() {
        val service = ProjectService(getRepository(false), getUserService())
        val coach = User("Lars Van Cauter", "lars@email.com", Role.Coach, "password")
        assertThrows<InvalidProjectIdException> {
            service.addCoachToProject(testProject.id, coach.id)
        }
    }

    @Test
    fun `removeStudentFromProject succeeds when student is in project`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getUserService())
        service.removeStudentFromProject(testProject.id, testStudent.id)
        verify { repository.save(testProject) }
    }

    @Test
    fun `removeStudentFromProject fails when student is not in project`() {
        val service = ProjectService(getRepository(true), getUserService())
        assertThrows<FailedOperationException> {
            service.removeStudentFromProject(testProject.id, UUID.randomUUID())
        }
    }

    @Test
    fun `removeCoachFromProject succeeds when coach is in project`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getUserService())
        service.removeCoachFromProject(testProject.id, testCoach.id)
        verify { repository.save(testProject) }
    }

    @Test
    fun `removeCoachFromProject fails when coach is not in project`() {
        val service = ProjectService(getRepository(true), getUserService())
        assertThrows<FailedOperationException> {
            service.removeCoachFromProject(testProject.id, UUID.randomUUID())
        }
    }

    @Test
    fun `getConflicts returns the correct result`() {
        val testStudent = Student("Lars", "Van Cauter", "", "")
        val testStudent2 = Student("Lars2", "Van Cauter2", "", "")
        val testStudent3 = Student("Lars3", "Van Cauter3", "", "")
        val testProjectConflict = Project("Test", "a test project", mutableListOf(testStudent))
        val testProjectConflict2 =
            Project("Test", "a test project", mutableListOf(testStudent, testStudent2))
        val testProjectConflict3 =
            Project("Test", "a test project", mutableListOf(testStudent2, testStudent3))
        val repository = getRepository(true)
        every { repository.findAll() } returns
            mutableListOf(testProjectConflict, testProjectConflict2, testProjectConflict3)
        val service = ProjectService(repository, getUserService())
        val conflictlist = service.getConflicts()
        assert(
            conflictlist[0] == ProjectService.Conflict(
                testStudent.id,
                mutableListOf(testProjectConflict.id, testProjectConflict2.id)
            )
        )
        assert(
            conflictlist[1] == ProjectService.Conflict(
                testStudent2.id,
                mutableListOf(testProjectConflict2.id, testProjectConflict3.id)
            )
        )
        assert(conflictlist.size == 2)
    }

    @Test
    fun `Conflicts dataclass one argument constructor test`() {
        val conflict = ProjectService.Conflict(testStudent.id)
        assert(conflict.student == testStudent.id)
        assert(conflict.projects == mutableListOf<UUID>())
    }
}
