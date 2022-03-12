package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.UserController
import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.UserService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(UserController::class)
class UserControllerTests(@Autowired val mockMvc: MockMvc) {
    @MockkBean
    private lateinit var userService: UserService

    private val testId = UUID.randomUUID()
    private val testUser = User("Test", "test@email.com", Role.Admin, "password")
    private val testUserJsonRepresentation = ObjectMapper().writeValueAsString(testUser)

    @Test
    fun `getAllUsers should not fail`() {
        every { userService.getAllUsers() } returns emptyList()
        mockMvc.perform(get("/users")).andExpect(status().isOk)
    }

    @Test
    fun `getUserById returns user if user with given id exists`() {
        every { userService.getUserById(testId) } returns testUser
        mockMvc.perform(get("/users/$testId"))
            .andExpect(status().isOk)
            .andExpect(content().json(testUserJsonRepresentation))
    }

    @Test
    fun `getUserById return 404 if the user with given id does not exist`() {
        every { userService.getUserById(testId) }.throws(InvalidIdException())
        mockMvc.perform(get("/users/$testId")).andExpect(status().isNotFound)
    }

    @Test
    fun `patchUser should not fail if the user exists`() {
        /*
         * We use any here because the json to object conversion will result in a different instance with the same data,
         * but we don't have a reference to that specific instance.
         */
        every { userService.patchUser(any()) } just Runs
        mockMvc.perform(
            patch("/users/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUserJsonRepresentation)
        ).andExpect(status().isOk)
    }

    @Test
    fun `patchUser should return 404 if the user does not exist`() {
        every { userService.patchUser(any()) }.throws(InvalidIdException())
        mockMvc.perform(
            patch("/users/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUserJsonRepresentation)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `deleteUser should not fail if the user exists`() {
        every { userService.deleteUserById(testId) } just Runs
        mockMvc.perform(delete("/users/$testId")).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteUser should return 404 if the user does not exist`() {
        every { userService.deleteUserById(testId) }.throws(InvalidIdException())
        mockMvc.perform(delete("/users/$testId")).andExpect(status().isNotFound)
    }

    @Test
    fun `postUser should not fail`() {
        every { userService.postUser(any()) } returns testUser.id
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUserJsonRepresentation)
        ).andExpect(status().isCreated)
    }

    @Test
    fun `postUserRole should not fail if the user exists`() {
        every { userService.changeRole(testId, Role.Admin) } just Runs
        mockMvc.perform(
            post("/users/$testId/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(Role.Admin))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `postUserRole should return 404 if the user does not exist`() {
        every { userService.changeRole(testId, Role.Admin) }.throws(InvalidIdException())
        mockMvc.perform(
            post("/users/$testId/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(Role.Admin))
        ).andExpect(status().isNotFound)
    }
}
