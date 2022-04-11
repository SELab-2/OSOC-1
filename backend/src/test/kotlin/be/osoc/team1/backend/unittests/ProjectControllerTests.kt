package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.ProjectController
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.ProjectService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@UnsecuredWebMvcTest(ProjectController::class)
class ProjectControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var projectService: ProjectService

    private val testId = UUID.randomUUID()
    private val testProject = Project("Proj", "Client", "desc")
    private val objectMapper = ObjectMapper()
    private val jsonRepresentation = objectMapper.writeValueAsString(testProject)

    @Test
    fun `getAllProjects should not fail`() {
        every { projectService.getAllProjects("") } returns emptyList()
        mockMvc.perform(get("/projects")).andExpect(status().isOk)
    }

    @Test
    fun `getAllProjects name filtering parses the correct name`() {
        val testList = listOf(Project("_", "_", "_"))
        val testList2 = listOf(Project("_2", "_2", "_2"))
        every { projectService.getAllProjects("lars") } returns testList
        every { projectService.getAllProjects("lars test") } returns testList2
        // tests the url parsing + with encoding
        mockMvc.perform(get("/projects?name=lars"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList)))
        mockMvc.perform(get("/projects?name=lars%20test"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testList2)))
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
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteProjectById returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.deleteProjectById(differentId) }.throws(InvalidIdException())
        mockMvc.perform(delete("/projects/$differentId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `postProject should return created project`() {
        every { projectService.postProject(any()) } returns testProject
        mockMvc.perform(
            post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRepresentation)
        ).andExpect(status().isCreated).andExpect(content().string(jsonRepresentation))
    }

    @Test
    fun `getStudentsOfProject succeeds if project with given id exists`() {
        every { projectService.getStudents(testId) } returns emptyList()
        mockMvc.perform(get("/projects/$testId/students"))
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun `getStudentsOfProject returns 404 Not Found if project with given id does not exist`() {
        val differentId = UUID.randomUUID()
        every { projectService.getStudents(differentId) }.throws(InvalidIdException())
        mockMvc.perform(get("/projects/$differentId/students"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `postStudentToProject succeeds if project with given id exists`() {
        val student = Student("Lars", "Van Cauter", "", "")
        every { projectService.addStudentToProject(testId, student) } just Runs
        every { studentService.getStudentById(student.id) } returns student
        mockMvc.perform(
            post("/projects/$testId/students/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student.id))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `postStudentToProject returns 404 Not Found if project with given id does not exist`() {
        val student = Student("Lars", "Van Cauter", "", "")
        val differentId = UUID.randomUUID()
        every { projectService.addStudentToProject(differentId, student) }.throws(InvalidIdException())
        every { studentService.getStudentById(student.id) } returns student
        mockMvc.perform(
            post("/projects/$differentId/students/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student.id))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteStudentOfProject succeeds if project with given id exists`() {
        val studentId = UUID.randomUUID()
        every { projectService.removeStudentFromProject(testId, studentId) } just Runs
        mockMvc.perform(delete("/projects/$testId/students/$studentId"))
            .andExpect(status().isNoContent)
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
        val notAssignedStudentId = UUID.randomUUID()
        every { projectService.removeStudentFromProject(testId, notAssignedStudentId) }.throws(
            FailedOperationException()
        )
        mockMvc.perform(delete("/projects/$testId/students/$notAssignedStudentId"))
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
        val coach = User("Lars Van Cauter", "lars@email.com", Role.Coach, "password")
        every { projectService.addCoachToProject(testId, any()) } just Runs
        mockMvc.perform(
            post("/projects/$testId/coaches/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coach.id))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `postCoachOfProject returns 404 Not Found if project with given id does not exist`() {
        val coach = User("Lars Van Cauter", "lars@email.com", Role.Coach, "password")
        val differentId = UUID.randomUUID()
        every { projectService.addCoachToProject(differentId, any()) }.throws(InvalidIdException())
        mockMvc.perform(
            post("/projects/$differentId/coaches/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coach.id))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteCoachOfProject succeeds if project with given id exists`() {
        val coachId = UUID.randomUUID()
        every { projectService.removeCoachFromProject(testId, coachId) } just Runs
        mockMvc.perform(delete("/projects/$testId/coaches/$coachId"))
            .andExpect(status().isNoContent)
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
        val notAssignedCoachId = UUID.randomUUID()
        every { projectService.removeCoachFromProject(testId, notAssignedCoachId) }.throws(FailedOperationException())
        mockMvc.perform(delete("/projects/$testId/coaches/$notAssignedCoachId"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `getProjectConflicts returns conflicts`() {
        // create a conflict
        val testStudent = Student("Lars", "Van Cauter", "", "")
        val testProjectConflict = Project("Test", "Client", "a test project")
        val testProjectConflict2 = Project("Test", "Client", "a test project")
        val result = mutableListOf(ProjectService.Conflict(testStudent.id, mutableListOf(testProjectConflict.id, testProjectConflict2.id)))
        every { projectService.getConflicts() } returns result
        mockMvc.perform(get("/projects/conflicts"))
            .andExpect(status().isOk)
            .andExpect(content().string(objectMapper.writeValueAsString(result)))
    }

    @Test
    fun `postAssignment succeeds if everything is correct`() {
        val assignmentPost = ProjectService.AssignmentPost(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "reason"
        )
        every { projectService.postAssignment(any(), any()) } just Runs
        mockMvc.perform(
            post("/projects/$testId/assignments")
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
        every { projectService.postAssignment(any(), any()) } throws InvalidIdException()
        mockMvc.perform(
            post("/projects/$testId/assignments")
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
        every { projectService.postAssignment(any(), any()) } throws ForbiddenOperationException()
        mockMvc.perform(
            post("/projects/$testId/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentPost))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `deleteAssignment succeeds if everything is correct`() {
        every { projectService.deleteAssignment(any(), any()) } just Runs
        mockMvc.perform(delete("/projects/$testId/assignments/$testId")).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteAssignment returns 404 if an invalid id is used`() {
        every { projectService.deleteAssignment(any(), any()) } throws InvalidIdException()
        mockMvc.perform(delete("/projects/$testId/assignments/$testId")).andExpect(status().isNotFound)
    }
}
