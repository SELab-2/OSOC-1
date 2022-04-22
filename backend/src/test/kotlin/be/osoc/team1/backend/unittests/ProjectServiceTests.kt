package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidAssignmentIdException
import be.osoc.team1.backend.exceptions.InvalidPositionIdException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.repositories.ProjectRepository
import be.osoc.team1.backend.services.ProjectService
import be.osoc.team1.backend.services.StudentService
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
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

class ProjectServiceTests {
    private val testId = UUID.randomUUID()
    private val testStudent = Student("Lars", "Van Cauter",)
    private val testCoach = User("Lars2 Van Cauter", "lars2@email.com", Role.Coach, "password")
    private val testEdition = "testEdition"
    private val testSkill = Skill("Backend")
    private val testSkill2 = Skill("Frontend")
    private val testProject = Project(
        "Test", "Client", "a test project", testEdition,
        mutableListOf(testCoach),
        listOf(Position(testSkill, 1), Position(testSkill2, 1))
    )
    private val savedProject = Project(
        "Saved", "Client", "a saved project", testEdition, mutableListOf(testCoach)
    )
    private val suggester = User("username", "email", Role.Coach, "password")

    private fun getRepository(projectAlreadyExists: Boolean): ProjectRepository {
        val repository: ProjectRepository = mockk()
        every { repository.existsByIdAndEdition(any(), testEdition) } returns projectAlreadyExists
        every { repository.findByIdAndEdition(any(), testEdition) } returns
            if (projectAlreadyExists) testProject else null
        every { repository.deleteById(any()) } just Runs
        every { repository.save(any()) } returns savedProject
        every { repository.findByEdition(testEdition) } returns listOf(testProject)
        return repository
    }

    private fun getStudentService(hasSkill: Boolean): StudentService {
        val service = mockk<StudentService>()
        val skills = mutableSetOf<Skill>()
        if (hasSkill) skills.add(testSkill)
        val student = Student("firstname", "lastname", skills,)
        every { service.getStudentById(any()) } returns student
        return service
    }

    private fun getUserService(user: User = testCoach): UserService {
        val service = mockk<UserService>()
        every { service.getUserById(user.id) } returns user
        return service
    }

    @Test
    fun `getAllProjects does not fail`() {
        val service = ProjectService(getRepository(true), mockk(), getUserService())
        assertEquals(listOf(testProject), service.getAllProjects(testEdition))
    }

    @Test
    fun `getAllProjects name filtering returns only projects with those names`() {
        val testProject = Project("Lars", "Client", "Cauter", testEdition)
        val testProject2 = Project("Sral", "Client", "Retuac", testEdition)
        val testProject3 = Project("Arsl", "Client", "Auterc", testEdition)
        val testProject4 = Project("Rsla", "Client", "Uterca", testEdition)
        val repository: ProjectRepository = mockk()
        val allProjects = listOf(testProject, testProject2, testProject3, testProject4)
        every { repository.findByEdition(testEdition) } returns allProjects
        val service = ProjectService(repository, mockk(), getUserService())
        assertEquals(listOf(testProject), service.getAllProjects(testEdition, "lars"))
        assertEquals(listOf(testProject, testProject3), service.getAllProjects(testEdition, "ars"))
        assertEquals(listOf<Project>(), service.getAllProjects(testEdition, "uter"))
        assertEquals(allProjects, service.getAllProjects(testEdition))
    }

    @Test
    fun `getProjectById succeeds when project with id exists`() {
        val service = ProjectService(getRepository(true), mockk(), getUserService())
        assertEquals(testProject, service.getProjectById(testId, testEdition))
    }

    @Test
    fun `getProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false), mockk(), getUserService())
        assertThrows<InvalidProjectIdException> { service.getProjectById(testId, testEdition) }
    }

    @Test
    fun `deleteProjectById succeeds when project with id exists`() {
        val repo = getRepository(true)
        val service = ProjectService(repo, mockk(), getUserService())
        service.deleteProjectById(testId, testEdition)
        verify { repo.deleteById(testId) }
    }

    @Test
    fun `deleteProjectById fails when no project with that id exists`() {
        val service = ProjectService(getRepository(false), mockk(), getUserService())
        assertThrows<InvalidProjectIdException> { service.deleteProjectById(testId, testEdition) }
    }

    @Test
    fun `postProject returns some other id than what was passed`() {
        val service = ProjectService(getRepository(false), mockk(), getUserService())
        assertNotEquals(service.postProject(testProject).id, testId)
    }

    @Test
    fun `patchProject updates project when project with same id exists`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, mockk(), getUserService())
        service.patchProject(testProject, testEdition)
        verify { repository.save(testProject) }
    }

    @Test
    fun `patchProject fails when no project with same id exists`() {
        val service = ProjectService(getRepository(false), mockk(), getUserService())
        assertThrows<InvalidProjectIdException> { service.patchProject(testProject, testEdition) }
    }

    @Test
    fun `addCoachToProject runs`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, mockk(), getUserService())
        service.addCoachToProject(testProject.id, testCoach.id, testEdition)
        verify { repository.save(testProject) }
    }

    @Test
    fun `addCoachToProject fails when project doesnt exit`() {
        val service = ProjectService(getRepository(false), mockk(), getUserService())
        val coach = User("Lars Van Cauter", "lars@email.com", Role.Coach, "password")
        assertThrows<InvalidProjectIdException> {
            service.addCoachToProject(testProject.id, coach.id, testEdition)
        }
    }

    @Test
    fun `removeCoachFromProject succeeds when coach is in project`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, mockk(), getUserService())
        service.removeCoachFromProject(testProject.id, testCoach.id, testEdition)
        verify { repository.save(testProject) }
    }

    @Test
    fun `removeCoachFromProject fails when coach is not in project`() {
        val service = ProjectService(getRepository(true), mockk(), getUserService())
        assertThrows<FailedOperationException> {
            service.removeCoachFromProject(testProject.id, UUID.randomUUID(), testEdition)
        }
    }

    @Test
    fun `getConflicts returns the correct result`() {
        val testStudent = Student("Lars", "Van Cauter",)
        val testStudent2 = Student("Lars2", "Van Cauter2",)
        val testStudent3 = Student("Lars3", "Van Cauter3",)
        val position = Position(Skill("backend"), 2)
        val suggester = User("suggester", "email", Role.Coach, "password")
        val testProjectConflict = Project(
            "Test", "Client", "a test project", testEdition,
            assignments = mutableListOf(
                Assignment(testStudent, position, suggester, "reason")
            )
        )
        val testProjectConflict2 = Project(
            "Test", "Client", "a test project", testEdition,
            assignments = mutableListOf(
                Assignment(testStudent, position, suggester, "reason"),
                Assignment(testStudent2, position, suggester, "reason")
            )
        )
        val testProjectConflict3 = Project(
            "Test", "Client", "a test project", testEdition,
            assignments = mutableListOf(
                Assignment(testStudent2, position, suggester, "reason"),
                Assignment(testStudent3, position, suggester, "reason")
            )
        )
        val repository = getRepository(true)
        every { repository.findByEdition(testEdition) } returns mutableListOf(testProjectConflict, testProjectConflict2, testProjectConflict3)
        val service = ProjectService(repository, mockk(), getUserService())

        val mockRequest = MockHttpServletRequest()
        mockRequest.scheme = "https"
        mockRequest.serverName = "example.com"
        mockRequest.serverPort = -1
        mockRequest.contextPath = "/api"
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(mockRequest))
        val conflictList = service.getConflicts(testEdition)
        assert(
            conflictList[0] == ProjectService.Conflict(
                "https://example.com/api/students/" + testStudent.id,
                mutableListOf(
                    "https://example.com/api/projects/" + testProjectConflict.id,
                    "https://example.com/api/projects/" + testProjectConflict2.id
                )
            )
        )
        assert(
            conflictList[1] == ProjectService.Conflict(
                "https://example.com/api/students/" + testStudent2.id,
                mutableListOf(
                    "https://example.com/api/projects/" + testProjectConflict2.id,
                    "https://example.com/api/projects/" + testProjectConflict3.id
                )
            )
        )
        assert(conflictList.size == 2)
    }

    @Test
    fun `Conflicts dataclass one argument constructor test`() {
        val conflict = ProjectService.Conflict("https://example.com/api/students/" + testStudent.id)
        assert(conflict.student == "https://example.com/api/students/" + testStudent.id)
        assert(conflict.projects == mutableListOf<String>())
    }

    @Test
    fun `postAssignment succeeds if everything is correct`() {
        val service = ProjectService(getRepository(true), getStudentService(true), getUserService(suggester))
        val assignmentPost = ProjectService.AssignmentPost(
            testStudent.id,
            testProject.positions.first().id,
            suggester.id,
            "reason"
        )
        service.postAssignment(testProject.id, assignmentPost, testEdition)
    }

    @Test
    fun `postAssignment fails if position is not part of project`() {
        val repository = getRepository(true)
        val service = ProjectService(repository, mockk(), mockk())
        val assignmentPost = ProjectService.AssignmentPost(
            testStudent.id,
            UUID.randomUUID(),
            suggester.id,
            "reason"
        )
        assertThrows<InvalidPositionIdException> { service.postAssignment(testProject.id, assignmentPost, testEdition) }
    }

    @Test
    fun `postAssignment fails if the student was already assigned this position on the project`() {
        val service = ProjectService(getRepository(true), getStudentService(true), getUserService(suggester))
        val assignmentPost = ProjectService.AssignmentPost(
            testStudent.id,
            testProject.positions.first().id,
            suggester.id,
            "reason"
        )
        val assignment = Assignment(testStudent, testProject.positions.first(), suggester, "reason")
        testProject.assignments.add(assignment)
        assertThrows<ForbiddenOperationException> { service.postAssignment(testProject.id, assignmentPost, testEdition) }
        testProject.assignments.remove(assignment)
    }

    @Test
    fun `postAssignment does not fail when assigned a different position on the project`() {
        val service = ProjectService(getRepository(true), getStudentService(true), getUserService(suggester))
        val positionIterator = testProject.positions.iterator()
        val assignmentPost = ProjectService.AssignmentPost(
            testStudent.id,
            positionIterator.next().id,
            suggester.id,
            "reason"
        )
        val assignment = Assignment(testStudent, positionIterator.next(), suggester, "reason")
        testProject.assignments.add(assignment)
        service.postAssignment(testProject.id, assignmentPost, testEdition)
        testProject.assignments.remove(assignment)
    }

    @Test
    fun `deleteAssignment fails if the assignment is not part of the project`() {
        val service = ProjectService(getRepository(true), getStudentService(true), getUserService(suggester))
        assertThrows<InvalidAssignmentIdException> { service.deleteAssignment(testProject.id, UUID.randomUUID(), testEdition) }
    }

    @Test
    fun `deleteAssignment does not fail if the assignment is part of the project`() {
        val assignmentRepository: AssignmentRepository = mockk()
        val service = ProjectService(
            getRepository(true),
            getStudentService(true),
            getUserService(suggester)
        )

        val assignment = Assignment(testStudent, testProject.positions.first(), suggester, "reason")
        every { assignmentRepository.findByIdOrNull(assignment.id) } returns assignment

        testProject.assignments.add(assignment)
        service.deleteAssignment(testProject.id, assignment.id, testEdition)
        testProject.assignments.remove(assignment)
    }

    @Test
    fun `getStudents(Project) does not fail`() {
        val service = ProjectService(
            getRepository(true),
            getStudentService(true),
            getUserService(suggester)
        )
        val testStudent = Student("Lars", "Van Cauter",)
        val testStudent2 = Student("Lars2", "Van Cauter2",)
        val position = Position(Skill("backend"), 2)
        val testProject = Project(
            "Test", "Client", "a test project", "",
            assignments = mutableListOf(
                Assignment(testStudent, position, suggester, "reason"),
                Assignment(testStudent2, position, suggester, "reason")
            )
        )
        assertEquals(mutableListOf(testStudent, testStudent2), service.getStudents(testProject))
    }

    @Test
    fun `getStudents(UUID) does not fail when project exists`() {
        val service = ProjectService(
            getRepository(true),
            getStudentService(true),
            getUserService(suggester)
        )
        assertEquals(mutableListOf<Student>(), service.getStudents(testProject.id, testEdition))
    }

    @Test
    fun `getStudents(UUID) fails when project doesn't exist`() {
        val service = ProjectService(
            getRepository(false),
            getStudentService(true),
            getUserService(suggester)
        )
        assertThrows<InvalidProjectIdException> { service.getStudents(testProject.id, testEdition) }
    }
}
