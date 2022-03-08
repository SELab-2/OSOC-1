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
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class ProjectServiceTests {
    private val testId = UUID.randomUUID()
    private val testStudent = Student("Lars","Van Cauter")
    private val testCoach = Coach("Lars2","Van Cauter")
    private val testProject = Project("Test", "a test project", mutableListOf(testStudent), mutableListOf(testCoach))
    private val savedProject = Project("Saved", "a saved project", mutableListOf(testStudent), mutableListOf(testCoach))

    private fun getService(projectAlreadyExists: Boolean): ProjectService {
        val repository: ProjectRepository = mockk()
        every { repository.existsById(any()) } returns projectAlreadyExists
        every { repository.findByIdOrNull(any()) } returns if (projectAlreadyExists) testProject else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns savedProject
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
    fun `addStudentToProject runs`() {
        val service = getService(true)
        val student = Student("Lars", "Van Cauter")
        service.addStudentToProject(testProject.id, student)
    }

    @Test(expected = InvalidIdException::class)
    fun `addStudentToProject fails when project doesnt exist`() {
        val service = getService(false)
        val student = Student("Lars", "Van Cauter")
        service.addStudentToProject(testProject.id, student)
    }

    @Test
    fun `addCoachToProject runs`() {
        val service = getService(true)
        val student = Coach("Lars", "Van Cauter")
        service.addCoachToProject(testProject.id, student)
    }

    @Test(expected = InvalidIdException::class)
    fun `addCoachToProject fails when project doesnt exit`() {
        val service = getService(false)
        val student = Coach("Lars", "Van Cauter")
        service.addCoachToProject(testProject.id, student)
    }

    @Test
    fun `removeStudentFromProject succeeds when student is in project`() {
        val service = getService(true)
        service.removeStudentFromProject(testProject.id, testStudent.id)
    }

    @Test(expected = FailedOperationException::class)
    fun `removeStudentFromProject fails when student is not in project`() {
        val service = getService(true)
        service.removeStudentFromProject(testProject.id, UUID.randomUUID())
    }

    @Test
    fun `removeCoachFromProject succeeds when student is in project`() {
        val service = getService(true)
        service.removeCoachFromProject(testProject.id, testCoach.id)
    }
    @Test(expected = FailedOperationException::class)
    fun `removeCoachFromProject fails when student is not in project`() {
        val service = getService(true)
        service.removeCoachFromProject(testProject.id, UUID.randomUUID())
    }
}