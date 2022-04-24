package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.ProjectController
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.PagedCollection
import be.osoc.team1.backend.services.ProjectService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

@UnsecuredWebMvcTest(ProjectController::class)
class ProjectControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var projectService: ProjectService

    private val testId = UUID.randomUUID()
    private val testEdition = "testEdition"
    private val editionUrl = "/$testEdition/projects"
    private val testProject = Project("Proj", "Client", "desc", testEdition)
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun beforeEach() {
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))
    }

    @Test
    fun `getAllProjects should not fail`() {
        every { projectService.getAllProjects(testEdition) } returns emptyList()
        mockMvc.perform(get(editionUrl)).andExpect(status().isOk)
    }

    @Test
    fun `getAllProjects paging returns the correct amount`() {
        val testList = listOf(testProject)
        every { projectService.getAllProjects(testEdition) } returns listOf(
            testProject,
            Project("Foo", "Bar", "desc", testEdition),
            Project("Fooo", "Baar", "desc", testEdition)
        )
        mockMvc.perform(get("$editionUrl?pageNumber=0&pageSize=1"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(testList, 3))))
    }
    @Test
    fun `getAllProjects name filtering parses the correct name`() {
        val testList = listOf(Project("_", "_", "_", testEdition))
        val testList2 = listOf(Project("_2", "_2", "_2", testEdition))
        every { projectService.getAllProjects(testEdition, "lars") } returns testList
        every { projectService.getAllProjects(testEdition, "lars test") } returns testList2
        // tests the url parsing + with encoding
        mockMvc.perform(get("$editionUrl?name=lars"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(testList, 1))))
        mockMvc.perform(get("$editionUrl?name=lars%20test"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(PagedCollection(testList2, 1))))
    }

    @Test
    fun `getProjectById returns project if project with given id exists`() {
        val jsonRepresentation = objectMapper.writeValueAsString(testProject)
        every { projectService.getProjectById(testId, testEdition) } returns testProject
        mockMvc.perform(get("$editionUrl/$testId")).andExpect(status().isOk)
            .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getProjectById returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.getProjectById(differentId, testEdition) }.throws(InvalidIdException())
        mockMvc.perform(get("$editionUrl/$differentId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteProjectById succeeds if project with given id exists`() {
        every { projectService.deleteProjectById(testId, testEdition) } just Runs
        mockMvc.perform(delete("$editionUrl/$testId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteProjectById returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.deleteProjectById(differentId, testEdition) }.throws(InvalidIdException())
        mockMvc.perform(delete("$editionUrl/$differentId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `postProject should return created project`() {
        val jsonRepresentation = objectMapper.writeValueAsString(testProject)
        every { projectService.postProject(any()) } returns testProject
        mockMvc.perform(
            post(editionUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isCreated).andExpect(content().string(jsonRepresentation))
    }

    @Test
    fun `getStudentsOfProject succeeds if project with given id exists`() {
        every { projectService.getStudents(testId, testEdition) } returns emptyList()
        mockMvc.perform(get("$editionUrl/$testId/students"))
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun `getStudentsOfProject returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.getStudents(differentId, testEdition) }.throws(InvalidIdException())
        mockMvc.perform(get("$editionUrl/$differentId/students"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getCoachOfProject succeeds if project with given id exists`() {
        every { projectService.getProjectById(testId, testEdition) } returns testProject
        mockMvc.perform(get("$editionUrl/$testId/coaches"))
            .andExpect(status().isOk)
    }

    @Test
    fun `getCoachOfProject returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.getProjectById(differentId, testEdition) }.throws(InvalidIdException())
        mockMvc.perform(get("$editionUrl/$differentId/coaches"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `postCoachOfProject succeeds if project with given id exists`() {
        val coach = User("Lars Van Cauter", "lars@email.com", Role.Coach, "password")
        every { projectService.addCoachToProject(testId, any(), testEdition) } just Runs
        mockMvc.perform(
            post("$editionUrl/$testId/coaches/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coach.id))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `postCoachOfProject returns 404 Not Found if project with given id does not exist`() {
        val coach = User("Lars Van Cauter", "lars@email.com", Role.Coach, "password")
        val differentId = UUID.randomUUID()
        every { projectService.addCoachToProject(differentId, any(), testEdition) }.throws(InvalidIdException())
        mockMvc.perform(
            post("$editionUrl/$differentId/coaches/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coach.id))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteCoachOfProject succeeds if project with given id exists`() {
        val coachId = UUID.randomUUID()
        every { projectService.removeCoachFromProject(testId, coachId, testEdition) } just Runs
        mockMvc.perform(delete("$editionUrl/$testId/coaches/$coachId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteCoachOfProject returns 404 Not Found if project with given id does not exist`() {
        val coachId = UUID.randomUUID()
        val differentId = UUID.randomUUID()
        every { projectService.removeCoachFromProject(differentId, coachId, testEdition) }.throws(InvalidIdException())
        mockMvc.perform(delete("$editionUrl/$differentId/coaches/$coachId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteCoachOfProject returns 400 if coach with given id is not assigned to project`() {
        val notAssignedCoachId = UUID.randomUUID()
        every { projectService.removeCoachFromProject(testId, notAssignedCoachId, testEdition) }.throws(FailedOperationException())
        mockMvc.perform(delete("$editionUrl/$testId/coaches/$notAssignedCoachId"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `getProjectConflicts returns conflicts`() {
        // create a conflict
        val testStudent = Student("Lars", "Van Cauter", "")
        val testProjectConflict = Project("Test", "Client", "a test project", testEdition)
        val testProjectConflict2 = Project("Test", "Client", "a test project", testEdition)
        val result = mutableListOf(
            ProjectService.Conflict(
                "https://example.com/api/students/" + testStudent.id,
                mutableListOf(
                    "https://example.com/api/projects/" + testProjectConflict.id,
                    "https://example.com/api/projects/" + testProjectConflict2.id
                )
            )
        )
        every { projectService.getConflicts(testEdition) } returns result
        mockMvc.perform(get("/$editionUrl/conflicts"))
    }

    @Test
    fun `postAssignment succeeds if everything is correct`() {
        val assignmentPost = ProjectService.AssignmentPost(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "reason"
        )
        every { projectService.postAssignment(any(), any(), testEdition) } just Runs
        mockMvc.perform(
            post("$editionUrl/$testId/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentPost))
        ).andExpect(status().isOk)
    }

    @Test
    fun `postAssignment returns 404 if an invalid id is used`() {
        val assignmentPost = ProjectService.AssignmentPost(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "reason"
        )
        every { projectService.postAssignment(any(), any(), testEdition) } throws InvalidIdException()
        mockMvc.perform(
            post("$editionUrl/$testId/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentPost))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `postAssignment returns 403 if some of the required conditions are not met`() {
        val assignmentPost = ProjectService.AssignmentPost(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "reason"
        )
        every { projectService.postAssignment(any(), any(), testEdition) } throws ForbiddenOperationException()
        mockMvc.perform(
            post("$editionUrl/$testId/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentPost))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `deleteAssignment succeeds if everything is correct`() {
        every { projectService.deleteAssignment(any(), any(), testEdition) } just Runs
        mockMvc.perform(delete("$editionUrl/$testId/assignments/$testId")).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteAssignment returns 404 if an invalid id is used`() {
        every { projectService.deleteAssignment(any(), any(), testEdition) } throws InvalidIdException()
        mockMvc.perform(delete("$editionUrl/$testId/assignments/$testId")).andExpect(status().isNotFound)
    }
}
