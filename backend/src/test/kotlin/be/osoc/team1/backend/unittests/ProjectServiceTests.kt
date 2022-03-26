package be.osoc.team1.backend.unittests

import InvalidRoleRequirementIdException
import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.RoleRequirement
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.repositories.RoleRequirementRepository
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.StudentService
import be.osoc.team1.backend.services.UserService
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
    private val testCoach = User("Lars2 Van Cauter", "lars2@email.com", Role.Coach, "password")
    private val testSkill = Skill("Backend")
    private val testProject = Project(
        "Test",
        "Client",
        "a test project",
        mutableListOf(testStudent),
        mutableListOf(testCoach),
        listOf(RoleRequirement(testSkill, 1))
    )
    private val savedProject = Project(
        "Saved",
        "Client",
        "a saved project",
        mutableListOf(testStudent),
        mutableListOf(testCoach)
    )
    private val suggester = User("username", "email", Role.Coach, "password")

    private fun getRepository(projectAlreadyExists: Boolean): ProjectRepository {
        val repository: ProjectRepository = mockk()
        every { repository.existsById(any()) } returns projectAlreadyExists
        every { repository.findByIdOrNull(any()) } returns if (projectAlreadyExists) testProject else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns savedProject
        every { repository.findAll() } returns listOf(testProject)
        return repository
    }

    private fun getRoleRepository(): RoleRequirementRepository {
        return mockk()
    }

    private fun getStudentService(hasSkill: Boolean): StudentService {
        val service = mockk<StudentService>()
        val skills = mutableSetOf<Skill>()
        if (hasSkill) skills.add(testSkill)
        val student = Student("firstname", "lastname", skills)
        every { service.getStudentById(any()) } returns student
        return service
    }

    fun getUserService(user: User): UserService {
        val service = mockk<UserService>()
        every { service.getUserById(user.id) } returns user
        return service
    }

    @Test
    fun `getAllProjects does not fail`() {
        val service = ProjectService(getRepository(true), getRoleRepository(), mockk(), mockk(), mockk())
        assertEquals(service.getAllProjects(), listOf(testProject))
    }

    @Test
    fun `getProjectById succeeds when project with id exists`() {
        val service = ProjectService(getRepository(true), getRoleRepository(), mockk(), mockk(), mockk())
        assertEquals(service.getProjectById(testId), testProject)
    }

    @Test
    fun `getProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false), getRoleRepository(), mockk(), mockk(), mockk())
        assertThrows<InvalidProjectIdException> { service.getProjectById(testId) }
    }

    @Test
    fun `deleteProjectById succeeds when project with id exists`() {
        val repo = getRepository(true)
        val service = ProjectService(repo, getRoleRepository(), mockk(), mockk(), mockk())
        service.deleteProjectById(testId)
        verify { repo.deleteById(testId) }
    }

    @Test
    fun `deleteProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false), getRoleRepository(), mockk(), mockk(), mockk())
        assertThrows<InvalidProjectIdException> { service.deleteProjectById(testId) }
    }

    @Test
    fun `postProject returns some other id than what was passed`() {
        val service = ProjectService(getRepository(false), getRoleRepository(), mockk(), mockk(), mockk())
        Assertions.assertNotEquals(service.postProject(testProject), testId)
    }

    @Test
    fun `patchProject updates project when project with same id exists`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getRoleRepository(), mockk(), mockk(), mockk())
        service.patchProject(testProject)
        verify { repository.save(testProject) }
    }

    @Test
    fun `patchProject fails when no project with same id exists`() {
        val service = ProjectService(getRepository(false), getRoleRepository(), mockk(), mockk(), mockk())
        assertThrows<InvalidProjectIdException> { service.patchProject(testProject) }
    }

    @Test
    fun `addStudentToProject runs`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getRoleRepository(), mockk(), mockk(), mockk())
        val student = Student("Lars", "Van Cauter")
        service.addStudentToProject(testProject.id, student)
        verify { repository.save(testProject) }
    }

    @Test
    fun `addStudentToProject fails when project doesnt exist`() {
        val service = ProjectService(getRepository(false), getRoleRepository(), mockk(), mockk(), mockk())
        val student = Student("Lars", "Van Cauter")
        assertThrows<InvalidProjectIdException> { service.addStudentToProject(testProject.id, student) }
    }

    @Test
    fun `addCoachToProject runs`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getRoleRepository(), mockk(), mockk(), mockk())
        val coach = User("Lars Van Cauter", "lars@email.com", Role.Coach, "password")
        service.addCoachToProject(testProject.id, coach)
        verify { repository.save(testProject) }
    }

    @Test
    fun `addCoachToProject fails when project doesnt exit`() {
        val service = ProjectService(getRepository(false), getRoleRepository(), mockk(), mockk(), mockk())
        val coach = User("Lars Van Cauter", "lars@email.com", Role.Coach, "password")
        assertThrows<InvalidProjectIdException> { service.addCoachToProject(testProject.id, coach) }
    }

    @Test
    fun `removeStudentFromProject succeeds when student is in project`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getRoleRepository(), mockk(), mockk(), mockk())
        service.removeStudentFromProject(testProject.id, testStudent.id)
        verify { repository.save(testProject) }
    }

    @Test
    fun `removeStudentFromProject fails when student is not in project`() {
        val service = ProjectService(getRepository(true), getRoleRepository(), mockk(), mockk(), mockk())
        assertThrows<FailedOperationException> { service.removeStudentFromProject(testProject.id, UUID.randomUUID()) }
    }

    @Test
    fun `removeCoachFromProject succeeds when coach is in project`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getRoleRepository(), mockk(), mockk(), mockk())
        service.removeCoachFromProject(testProject.id, testCoach.id)
        verify { repository.save(testProject) }
    }

    @Test
    fun `removeCoachFromProject fails when coach is not in project`() {
        val service = ProjectService(getRepository(true), getRoleRepository(), mockk(), mockk(), mockk())
        assertThrows<FailedOperationException> { service.removeCoachFromProject(testProject.id, UUID.randomUUID()) }
    }

    @Test
    fun `getConflicts returns the correct result`() {
        val testStudent = Student("Lars", "Van Cauter")
        val testStudent2 = Student("Lars2", "Van Cauter2")
        val testStudent3 = Student("Lars3", "Van Cauter3")
        val testProjectConflict = Project("Test", "Client", "a test project", mutableListOf(testStudent))
        val testProjectConflict2 = Project("Test", "Client", "a test project", mutableListOf(testStudent, testStudent2))
        val testProjectConflict3 = Project(
            "Test",
            "Client",
            "a test project",
            mutableListOf(testStudent2, testStudent3)
        )
        val repository = getRepository(true)
        every { repository.findAll() } returns mutableListOf(testProjectConflict, testProjectConflict2, testProjectConflict3)
        val service = ProjectService(repository, getRoleRepository(), mockk(), mockk(), mockk())
        val conflictlist = service.getConflicts()
        assert(conflictlist[0] == ProjectService.Conflict(testStudent.id, mutableListOf(testProjectConflict.id, testProjectConflict2.id)))
        assert(conflictlist[1] == ProjectService.Conflict(testStudent2.id, mutableListOf(testProjectConflict2.id, testProjectConflict3.id)))
        assert(conflictlist.size == 2)
    }

    @Test
    fun `Conflicts dataclass one argument constructor test`() {
        val conflict = ProjectService.Conflict(testStudent.id)
        assert(conflict.student == testStudent.id)
        assert(conflict.projects == mutableListOf<UUID>())
    }

    @Test
    fun `postAssignment succeeds if everything is correct`() {
        val service = ProjectService(getRepository(true), getRoleRepository(), mockk(), getStudentService(true), getUserService(suggester))
        val assignmentPost = ProjectService.AssignmentPost(
            testStudent.id,
            testProject.requiredRoles[0].id,
            suggester.id,
            "reason"
        )
        service.postAssignment(testProject.id, assignmentPost)
    }

    @Test
    fun `postAssignment fails if role is not part of project`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, getRoleRepository(), mockk(), mockk(), mockk())
        val assignmentPost = ProjectService.AssignmentPost(
            testStudent.id,
            UUID.randomUUID(),
            suggester.id,
            "reason"
        )
        assertThrows<InvalidRoleRequirementIdException> { service.postAssignment(testProject.id, assignmentPost) }
    }

    @Test
    fun `postAssignment fails if the student doesn't have the required skill`() {
        val service = ProjectService(getRepository(true), getRoleRepository(), mockk(), getStudentService(false), getUserService(suggester))
        val assignmentPost = ProjectService.AssignmentPost(
            testStudent.id,
            testProject.requiredRoles[0].id,
            suggester.id,
            "reason"
        )
        assertThrows<ForbiddenOperationException> { service.postAssignment(testProject.id, assignmentPost) }
    }

    @Test
    fun `postAssignment fails if the student was already assigned a role on this project`() {
        val service = ProjectService(getRepository(true), getRoleRepository(), mockk(), getStudentService(true), getUserService(suggester))
        val assignmentPost = ProjectService.AssignmentPost(
            testStudent.id,
            testProject.requiredRoles[0].id,
            suggester.id,
            "reason"
        )
        val assignment = Assignment(testStudent, testProject.requiredRoles[0], suggester, "reason")
        testProject.assignments.add(assignment)
        assertThrows<ForbiddenOperationException> { service.postAssignment(testProject.id, assignmentPost) }
        testProject.assignments.remove(assignment)
    }

    @Test
    fun `postAssignment fails if the role already has enough assignees`() {
        val service = ProjectService(getRepository(true), getRoleRepository(), mockk(), getStudentService(true), getUserService(suggester))
        val assignmentPost = ProjectService.AssignmentPost(
            testStudent.id,
            testProject.requiredRoles[0].id,
            suggester.id,
            "reason"
        )
        val differentStudent = Student("Maarten", "Steevens")
        val assignment = Assignment(differentStudent, testProject.requiredRoles[0], suggester, "reason")
        testProject.assignments.add(assignment)
        assertThrows<ForbiddenOperationException> { service.postAssignment(testProject.id, assignmentPost) }
        testProject.assignments.remove(assignment)
    }
}
