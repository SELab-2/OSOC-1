package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.services.ProjectService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class ProjectServiceTests {
    private val testId = UUID.randomUUID()
    private val testProject = Project(testId, "Test", "a test project", mutableListOf())
    private val savedProject = Project(UUID.randomUUID(), "Saved", "a saved project", mutableListOf())

    private fun getService(projectAlreadyExists: Boolean): ProjectService {
        val repository: ProjectRepository = mockk()
        every { repository.existsById(testId) } returns projectAlreadyExists
        every { repository.findByIdOrNull(testId) } returns if (projectAlreadyExists) testProject else null
        every { repository.deleteById(testId) } just Runs
        every { repository.save(testProject) } returns savedProject
        return ProjectService(repository)
    }

    @Test
    fun `getProjectById succeeds when project with id exists`() {
        val service = getService(true)
        service.getProjectById(testId)
    }

    @Test(expected = InvalidIdException::class)
    fun `getProjectById fails when no project with that id exists`() {
        val service = getService(false)
        service.getProjectById(testId)
    }

    @Test
    fun `deleteProjectById succeeds when project with id exists`() {
        val service = getService(true)
        service.deleteProjectById(testId)
    }

    @Test(expected = InvalidIdException::class)
    fun `deleteProjectById fails when no project with that id exists`() {
        val service = getService(false)
        service.deleteProjectById(testId)
    }

    @Test
    fun `putProject returns some other id than what was passed`() {
        val service = getService(false)
        Assertions.assertNotEquals(service.putProject(testProject), testId)
    }

    @Test
    fun `patchProject updates project when project with same id exists`() {
        val service = getService(true)
        service.patchProject(testProject)
    }

    @Test(expected = InvalidIdException::class)
    fun `patchProject fails when no project with same id exists`() {
        val service = getService(false)
        service.patchProject(testProject)
    }

    @Test
    fun `addStudentToProject adds a student`(){

    }
}