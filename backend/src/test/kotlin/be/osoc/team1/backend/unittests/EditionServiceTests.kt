package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.EditionRepository
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.StudentService
import be.osoc.team1.backend.services.UserService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull

class EditionServiceTests {

    private val editionName = "edition"
    private val activeEdition = Edition(editionName, true)
    private val inactiveEdition = Edition(editionName, false)

    @Test
    fun `createInactiveEdition creates inactive edition`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(editionName) } returns false
        every { repository.save(inactiveEdition) } returns inactiveEdition
        val service = EditionService(repository, mockk(), mockk(), mockk())
        service.createInactiveEdition(editionName)
        verify { repository.save(inactiveEdition) }
    }

    @Test
    fun `createInactiveEdition fails if the edition already exists`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(editionName) } returns true
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertThrows<FailedOperationException> { service.createInactiveEdition(editionName) }
    }

    @Test
    fun `makeEditionInactive inactivates edition and disables coach accounts`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(activeEdition.name) } returns activeEdition
        every { repository.save(inactiveEdition) } returns inactiveEdition
        val coach = User("", "", Role.Coach, "")
        val admin = User("", "", Role.Admin, "")
        val userService = mockk<UserService>()
        every { userService.getAllUsers() } returns listOf(coach, admin)
        every { userService.changeRole(coach.id, Role.Disabled) } just Runs
        val service = EditionService(repository, mockk(), mockk(), userService)
        service.makeEditionInactive(activeEdition.name)
        verify { repository.save(inactiveEdition) }
        verify { userService.changeRole(coach.id, Role.Disabled) }
        verify(exactly = 0) { userService.changeRole(admin.id, Role.Disabled) }
    }

    @Test
    fun `makeEditionInactive fails if the edition does not exist`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(activeEdition.name) } returns null
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertThrows<InvalidIdException> { service.makeEditionInactive(activeEdition.name) }
    }

    @Test
    fun `makeEditionInactive fails if edition is already inactive`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(inactiveEdition.name) } returns inactiveEdition
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertThrows<FailedOperationException> { service.makeEditionInactive(inactiveEdition.name) }
    }

    @Test
    fun `makeEditionActive activates edition if it is inactive and there is no other active edition`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(inactiveEdition.name) } returns inactiveEdition
        every { repository.findAll() } returns listOf(inactiveEdition)
        every { repository.save(activeEdition) } returns activeEdition
        val service = EditionService(repository, mockk(), mockk(), mockk())
        service.makeEditionActive(inactiveEdition.name)
        verify { repository.save(activeEdition) }
    }

    @Test
    fun `makeEditionActive fails if the edition does not exist`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(editionName) } returns null
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertThrows<InvalidIdException> { service.makeEditionActive(editionName) }
    }

    @Test
    fun `makeEditionActive fails if the edition is already active`() {
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(activeEdition.name) } returns activeEdition
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertThrows<FailedOperationException> { service.makeEditionActive(activeEdition.name) }
    }

    @Test
    fun `makeEditionActive fails if the edition is inactive but there is another active edition`() {
        val activeEditionWithDifferentName = Edition("another edition", true)
        val repository = mockk<EditionRepository>()
        every { repository.findByIdOrNull(inactiveEdition.name) } returns inactiveEdition
        every { repository.findAll() } returns listOf(activeEditionWithDifferentName, inactiveEdition)
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertThrows<ForbiddenOperationException> { service.makeEditionActive(inactiveEdition.name) }
    }

    @Test
    fun `getActiveEdition returns the active edition if it exists`() {
        val repository = mockk<EditionRepository>()
        every { repository.findAll() } returns listOf(activeEdition)
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertEquals(activeEdition, service.getActiveEdition())
    }

    @Test
    fun `getActiveEdition returns null if there is no active edition`() {
        val repository = mockk<EditionRepository>()
        every { repository.findAll() } returns listOf(inactiveEdition)
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertEquals(null, service.getActiveEdition())
    }

    @Test
    fun `getInactiveEditions returns all inactive editions`() {
        val repository = mockk<EditionRepository>()
        every { repository.findAll() } returns listOf(activeEdition, inactiveEdition)
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertEquals(listOf(inactiveEdition), service.getInactiveEditions())
    }

    @Test
    fun `deleteEdition deletes edition if it exists`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(inactiveEdition.name) } returns true
        every { repository.deleteById(inactiveEdition.name) } just Runs
        val testStudent = Student("Tom", "Alard")
        val studentService = mockk<StudentService>()
        every { studentService.getAllStudents(Sort.unsorted(), inactiveEdition.name) } returns listOf(testStudent)
        every { studentService.deleteStudentById(testStudent.id) } just Runs
        val testProject = Project("", "", "")
        val projectService = mockk<ProjectService>()
        every { projectService.getAllProjects(inactiveEdition.name) } returns listOf(testProject)
        every { projectService.deleteProjectById(testProject.id, inactiveEdition.name) } just Runs
        val service = EditionService(repository, studentService, projectService, mockk())
        service.deleteEdition(inactiveEdition.name)
        verify { studentService.deleteStudentById(testStudent.id) }
        verify { projectService.deleteProjectById(testProject.id, inactiveEdition.name) }
        verify { repository.deleteById(inactiveEdition.name) }
    }

    @Test
    fun `deleteEdition fails if edition does not exist`() {
        val repository = mockk<EditionRepository>()
        every { repository.existsById(inactiveEdition.name) } returns false
        val service = EditionService(repository, mockk(), mockk(), mockk())
        assertThrows<InvalidIdException> { service.deleteEdition(inactiveEdition.name) }
    }
}
