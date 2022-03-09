package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Coach
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.services.ProjectService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class ProjectServiceTests {
    private val testId = UUID.randomUUID()
    private val testStudent = Student("Lars", "Van Cauter")
    private val testCoach = Coach("Lars2", "Van Cauter")
    private val testProject = Project("Test", "a test project", mutableListOf(testStudent), mutableListOf(testCoach))
    private val savedProject = Project("Saved", "a saved project", mutableListOf(testStudent), mutableListOf(testCoach))

    private fun getRepository(projectAlreadyExists: Boolean): ProjectRepository {
        val repository: ProjectRepository = mockk()
        every { repository.existsById(any()) } returns projectAlreadyExists
        every { repository.findByIdOrNull(any()) } returns if (projectAlreadyExists) testProject else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns savedProject
        return repository
    }

    @Test
    fun `getProjectById succeeds when project with id exists`() {
        val service = ProjectService(getRepository(true))
        assertEquals(service.getProjectById(testId), testProject)
    }

    @Test(expected = InvalidIdException::class)
    fun `getProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false))
        service.getProjectById(testId)
    }

    @Test
    fun `deleteProjectById succeeds when project with id exists`() {
        val repo = getRepository(true)
        val service = ProjectService(repo)
        service.deleteProjectById(testId)
        verify { repo.deleteById(testId) }
    }

    @Test(expected = InvalidIdException::class)
    fun `deleteProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false))
        service.deleteProjectById(testId)
    }

    @Test
    fun `putProject returns some other id than what was passed`() {
        val service = ProjectService(getRepository(false))
        Assertions.assertNotEquals(service.putProject(testProject), testId)
    }

    @Test
    fun `patchProject updates project when project with same id exists`() {
        val service = ProjectService(getRepository(true))
        service.patchProject(testProject)
    }

    @Test(expected = InvalidIdException::class)
    fun `patchProject fails when no project with same id exists`() {
        val service = ProjectService(getRepository(false))
        service.patchProject(testProject)
    }

    @Test
    fun `addStudentToProject runs`() {
        val service = ProjectService(getRepository(true))
        val student = Student("Lars", "Van Cauter")
        service.addStudentToProject(testProject.id, student)
    }

    @Test(expected = InvalidIdException::class)
    fun `addStudentToProject fails when project doesnt exist`() {
        val service = ProjectService(getRepository(false))
        val student = Student("Lars", "Van Cauter")
        service.addStudentToProject(testProject.id, student)
    }

    @Test
    fun `addCoachToProject runs`() {
        val service = ProjectService(getRepository(true))
        val student = Coach("Lars", "Van Cauter")
        service.addCoachToProject(testProject.id, student)
    }

    @Test(expected = InvalidIdException::class)
    fun `addCoachToProject fails when project doesnt exit`() {
        val service = ProjectService(getRepository(false))
        val student = Coach("Lars", "Van Cauter")
        service.addCoachToProject(testProject.id, student)
    }

    @Test
    fun `removeStudentFromProject succeeds when student is in project`() {
        val service = ProjectService(getRepository(true))
        service.removeStudentFromProject(testProject.id, testStudent.id)
    }

    @Test(expected = FailedOperationException::class)
    fun `removeStudentFromProject fails when student is not in project`() {
        val service = ProjectService(getRepository(true))
        service.removeStudentFromProject(testProject.id, UUID.randomUUID())
    }

    @Test
    fun `removeCoachFromProject succeeds when student is in project`() {
        val service = ProjectService(getRepository(true))
        service.removeCoachFromProject(testProject.id, testCoach.id)
    }

    @Test(expected = FailedOperationException::class)
    fun `removeCoachFromProject fails when student is not in project`() {
        val service = ProjectService(getRepository(true))
        service.removeCoachFromProject(testProject.id, UUID.randomUUID())
    }
}
