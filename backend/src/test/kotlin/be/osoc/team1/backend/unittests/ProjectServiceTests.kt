package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Coach
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.services.ProjectService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Test
    fun `getProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false))
        assertThrows<InvalidProjectIdException> { service.getProjectById(testId) }
    }

    @Test
    fun `deleteProjectById succeeds when project with id exists`() {
        val repo = getRepository(true)
        val service = ProjectService(repo)
        service.deleteProjectById(testId)
        verify { repo.deleteById(testId) }
    }

    @Test
    fun `deleteProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false))
        assertThrows<InvalidProjectIdException> { service.deleteProjectById(testId) }
    }

    @Test
    fun `postProject returns some other id than what was passed`() {
        val service = ProjectService(getRepository(false))
        Assertions.assertNotEquals(service.postProject(testProject), testId)
    }

    @Test
    fun `patchProject updates project when project with same id exists`() {
        val repository = getRepository(true)
        val service = ProjectService(repository)
        service.patchProject(testProject)
        verify { repository.save(testProject) }
    }

    @Test
    fun `patchProject fails when no project with same id exists`() {
        val service = ProjectService(getRepository(false))
        assertThrows<InvalidProjectIdException> { service.patchProject(testProject) }
    }

    @Test
    fun `addStudentToProject runs`() {
        val repository = getRepository(true)
        val service = ProjectService(repository)
        val student = Student("Lars", "Van Cauter")
        service.addStudentToProject(testProject.id, student)
        verify { repository.save(testProject) }
    }

    @Test
    fun `addStudentToProject fails when project doesnt exist`() {
        val service = ProjectService(getRepository(false))
        val student = Student("Lars", "Van Cauter")
        assertThrows<InvalidProjectIdException> { service.addStudentToProject(testProject.id, student) }
    }

    @Test
    fun `addCoachToProject runs`() {
        val repository = getRepository(true)
        val service = ProjectService(repository)
        val student = Coach("Lars", "Van Cauter")
        service.addCoachToProject(testProject.id, student)
        verify { repository.save(testProject) }
    }

    @Test
    fun `addCoachToProject fails when project doesnt exit`() {
        val service = ProjectService(getRepository(false))
        val student = Coach("Lars", "Van Cauter")
        assertThrows<InvalidProjectIdException> { service.addCoachToProject(testProject.id, student) }
    }

    @Test
    fun `removeStudentFromProject succeeds when student is in project`() {
        val repository = getRepository(true)
        val service = ProjectService(repository)
        service.removeStudentFromProject(testProject.id, testStudent.id)
        verify { repository.save(testProject) }
    }

    @Test
    fun `removeStudentFromProject fails when student is not in project`() {
        val service = ProjectService(getRepository(true))
        assertThrows<FailedOperationException> { service.removeStudentFromProject(testProject.id, UUID.randomUUID()) }
    }

    @Test
    fun `removeCoachFromProject succeeds when coach is in project`() {
        val repository = getRepository(true)
        val service = ProjectService(repository)
        service.removeCoachFromProject(testProject.id, testCoach.id)
        verify { repository.save(testProject) }
    }

    @Test
    fun `removeCoachFromProject fails when coach is not in project`() {
        val service = ProjectService(getRepository(true))
        assertThrows<FailedOperationException> { service.removeCoachFromProject(testProject.id, UUID.randomUUID()) }
    }

    @Test
    fun `getConflicts returns the correct result`() {
        val testStudent = Student("Lars", "Van Cauter")
        val testProjectConflict = Project("Test", "a test project", mutableListOf(testStudent))
        val testProjectConflict2 = Project("Test", "a test project", mutableListOf(testStudent))
        val repository = getRepository(true)
        every { repository.findAll() } returns mutableListOf(testProjectConflict, testProjectConflict2)
        val service = ProjectService(repository)
        val conflictlist = service.getConflicts()
        assert(conflictlist[0] == ProjectService.Conflict(testStudent.id, mutableListOf(testProjectConflict.id, testProjectConflict2.id)))
    }
}
