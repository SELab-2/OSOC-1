package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.ProjectController
import be.osoc.team1.backend.entities.Coach
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.ProjectService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(ProjectController::class)
class ProjectControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var projectService: ProjectService

    private val testId = UUID.randomUUID()
    private val testProject = Project("Proj", "desc")
    private val objectMapper = ObjectMapper()
    private val jsonRepresentation = objectMapper.writeValueAsString(testProject)

    @Test
    fun `getAllProjects should not fail`() {
        every { projectService.getAllProjects() } returns emptyList()
        mockMvc.perform(get("/projects")).andExpect(status().isOk)
    }

    @Test
    fun `getProjectById returns project if project with given id exists`() {
        every { projectService.getProjectById(testId) } returns testProject
        mockMvc.perform(get("/projects/$testId")).andExpect(status().isOk)
            .andExpect(content().json(jsonRepresentation))
    }

    @Test
    fun `getProjectById returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.getProjectById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("/projects/$differentId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteProjectById succeeds if project with given id exists`() {
        every { projectService.deleteProjectById(testId) } just Runs
        mockMvc.perform(delete("/projects/$testId"))
            .andExpect(status().isOk)
    }

    @Test
    fun `deleteProjectById returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.deleteProjectById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(delete("/projects/$differentId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `postProject should not fail`() {
        val databaseId = UUID.randomUUID()
        every { projectService.postProject(any()) } returns databaseId
        mockMvc.perform(
            post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isOk)
            .andExpect(content().string("\"$databaseId\""))
    }

    @Test
    fun `getStudentsOfProject succeeds if project with given id exists`() {
        every { projectService.getProjectById(testId) } returns testProject
        mockMvc.perform(get("/projects/$testId/students"))
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun `getStudentsOfProject returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.getProjectById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("/projects/$differentId/students"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `postStudentOfProject succeeds if project with given id exists`() {
        val student = Student("Lars", "Van Cauter")
        every { projectService.addStudentToProject(testId, any()) } just Runs
        mockMvc.perform(
            post("/projects/$testId/students/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student))
        ).andExpect(status().isOk)
    }

    @Test
    fun `postStudentOfProject returns 404 Not Found if project with given id does not exist`() {
        val student = Student("Lars", "Van Cauter")
        val differentId = UUID.randomUUID()
        every { projectService.addStudentToProject(differentId, any()) }.throws(InvalidIdException())
        mockMvc.perform(
            post("/projects/$differentId/students/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentOfProject succeeds if project with given id exists`() {
        val studentId = UUID.randomUUID()
        every { projectService.removeStudentFromProject(testId, studentId) } just Runs
        mockMvc.perform(delete("/projects/$testId/students/$studentId"))
            .andExpect(status().isOk)
    }

    @Test
    fun `deleteStudentOfProject returns 404 Not Found if project with given id does not exist`() {
        val studentId = UUID.randomUUID()
        val differentId = UUID.randomUUID()
        every { projectService.removeStudentFromProject(differentId, studentId) }.throws(InvalidIdException())
        mockMvc.perform(delete("/projects/$differentId/students/$studentId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentOfProject returns 400 if coach with given id is not assigned to project`() {
        val not_assigned_studentId = UUID.randomUUID()
        every { projectService.removeStudentFromProject(testId, not_assigned_studentId) }.throws(
            FailedOperationException()
        )
        mockMvc.perform(delete("/projects/$testId/students/$not_assigned_studentId"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `getCoachOfProject succeeds if project with given id exists`() {
        every { projectService.getProjectById(testId) } returns testProject
        mockMvc.perform(get("/projects/$testId/coaches"))
            .andExpect(status().isOk)
    }

    @Test
    fun `getCoachOfProject returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.getProjectById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("/projects/$differentId/coaches"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `postCoachOfProject succeeds if project with given id exists`() {
        val coach = Coach("Lars", "Van Cauter")
        every { projectService.addCoachToProject(testId, any()) } just Runs
        mockMvc.perform(
            post("/projects/$testId/coaches/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coach))
        ).andExpect(status().isOk)
    }

    @Test
    fun `postCoachOfProject returns 404 Not Found if project with given id does not exist`() {
        val coach = Coach("Lars", "Van Cauter")
        val differentId = UUID.randomUUID()
        every { projectService.addCoachToProject(differentId, any()) }.throws(InvalidIdException())
        mockMvc.perform(
            post("/projects/$differentId/coaches/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coach))
        ).andExpect(status().isNotFound)
    }
    @Test
    fun `deleteCoachOfProject succeeds if project with given id exists`() {
        val coachId = UUID.randomUUID()
        every { projectService.removeCoachFromProject(testId, coachId) } just Runs
        mockMvc.perform(delete("/projects/$testId/coaches/$coachId"))
            .andExpect(status().isOk)
    }

    @Test
    fun `deleteCoachOfProject returns 404 Not Found if project with given id does not exist`() {
        val coachId = UUID.randomUUID()
        val differentId = UUID.randomUUID()
        every { projectService.removeCoachFromProject(differentId, coachId) }.throws(InvalidIdException())
        mockMvc.perform(delete("/projects/$differentId/coaches/$coachId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteCoachOfProject returns 400 if coach with given id is not assigned to project`() {
        val not_assigned_coachId = UUID.randomUUID()
        every { projectService.removeCoachFromProject(testId, not_assigned_coachId) }.throws(FailedOperationException())
        mockMvc.perform(delete("/projects/$testId/coaches/$not_assigned_coachId"))
            .andExpect(status().isBadRequest)
    }
}
