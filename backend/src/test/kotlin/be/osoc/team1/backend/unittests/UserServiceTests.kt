package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.services.UserService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTests {
    private val testUser = User("Test", "test@email.com", Role.Admin, "password")
    private val testId = testUser.id

    private fun getRepository(userAlreadyExists: Boolean): UserRepository {
        val repository: UserRepository = mockk()
        every { repository.existsById(testId) } returns userAlreadyExists
        every { repository.findByIdOrNull(testId) } returns if (userAlreadyExists) testUser else null
        every { repository.deleteById(testId) } just Runs
        every { repository.save(testUser) } returns testUser
        every { repository.findAll() } returns listOf(testUser)
        return repository
    }

    private fun getPasswordEncoder(): PasswordEncoder {
        val passwordEncoder: PasswordEncoder = mockk()
        every { passwordEncoder.encode(any()) } returns "Encoded password"
        return passwordEncoder
    }

    @Test
    fun `getAllUsers does not fail`() {
        val service = UserService(getRepository(true), getPasswordEncoder())
        assertEquals(service.getAllUsers(), listOf(testUser))
    }

    @Test
    fun `getUserById does not fail when the user with id exists`() {
        val service = UserService(getRepository(true), getPasswordEncoder())
        assertEquals(service.getUserById(testId), testUser)
    }

    @Test
    fun `getUserById fails when the user with id does not exist`() {
        val service = UserService(getRepository(false), getPasswordEncoder())
        assertThrows<InvalidIdException> { service.getUserById(testId) }
    }

    @Test
    fun `deleteUserById does not fail when the user with id exists`() {
        val repository = getRepository(true)
        val service = UserService(repository, getPasswordEncoder())
        service.deleteUserById(testId)
        verify { repository.deleteById(testId) }
    }

    @Test
    fun `deleteUserById fails when the user with id does not exist`() {
        val service = UserService(getRepository(false), getPasswordEncoder())
        assertThrows<InvalidIdException> { service.deleteUserById(testId) }
    }

    @Test
    fun `registerUser does not fail if there is not already a user with the same email in the database`() {
        val repository = getRepository(true)
        val service = UserService(repository, getPasswordEncoder())

        val slot = slot<User>()
        every { repository.save(capture(slot)) } returns User("username", "password", Role.Disabled, "password")

        service.registerUser("username", "email", "password")

        verify { repository.save(any()) }
        val capturedUser = slot.captured
        assertEquals(capturedUser.username, "username")
        assertEquals(capturedUser.email, "email")
        assertEquals(capturedUser.role, Role.Disabled)
        assertEquals(capturedUser.password, "Encoded password")
    }

    @Test
    fun `changeRole does not fail when a user with id exists and changes the role`() {
        val repository = getRepository(true)
        val otherAdmin = User("Other admin", "otherAdmin@email.com", Role.Admin, "password")
        every { repository.findByRole(Role.Admin) } returns listOf(testUser, otherAdmin)
        val service = UserService(repository, getPasswordEncoder())
        service.changeRole(testId, Role.Coach)
        verify { repository.save(testUser) }
        assertEquals(testUser.role, Role.Coach)
        service.changeRole(testId, Role.Admin)
    }

    @Test
    fun `changeRole fails when demoting the last admin`() {
        val repository = getRepository(true)
        every { repository.findByRole(Role.Admin) } returns listOf(testUser)
        val service = UserService(repository, getPasswordEncoder())
        assertThrows<ForbiddenOperationException> { service.changeRole(testId, Role.Coach) }
    }

    @Test
    fun `changeRole fails when no user with id exists`() {
        val service = UserService(getRepository(false), getPasswordEncoder())
        assertThrows<InvalidIdException> { service.changeRole(testId, Role.Coach) }
    }

    @Test
    fun `patchUser does not fail when a user with id exists`() {
        val repository = getRepository(true)
        val service = UserService(repository, getPasswordEncoder())
        service.patchUser(testUser)
        verify { repository.save(testUser) }
    }

    @Test
    fun `patchUser fails when no user with id exists`() {
        val service = UserService(getRepository(false), getPasswordEncoder())
        assertThrows<InvalidIdException> { service.patchUser(testUser) }
    }
}
