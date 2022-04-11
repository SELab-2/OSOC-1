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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTests {
    private val testOrganization = "test_organization"
    private val testUser = User("Test", "test@email.com", Role.Admin, "password", testOrganization)
    private val testId = testUser.id

    private fun getRepository(userAlreadyExists: Boolean): UserRepository {
        val repository: UserRepository = mockk()
        every { repository.existsById(testId) } returns userAlreadyExists
        every { repository.findByIdOrNull(testId) } returns if (userAlreadyExists) testUser else null
        every { repository.deleteById(testId) } just Runs
        every { repository.save(testUser) } returns testUser
        every { repository.findByOrganization(testOrganization) } returns listOf(testUser)
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
        assertEquals(service.getAllUsers(testOrganization), listOf(testUser))
    }

    @Test
    fun `getUserById does not fail when the user with id exists`() {
        val service = UserService(getRepository(true), getPasswordEncoder())
        assertEquals(testUser, service.getUserById(testId))
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
        val testUser2 = User("username", "email", Role.Disabled, "password")
        every { repository.save(capture(slot)) } returns testUser2

        service.registerUser(testUser2, "organization")

        verify { repository.save(any()) }
        val capturedUser = slot.captured
        assertEquals("username", capturedUser.username)
        assertEquals("email", capturedUser.email)
        assertEquals(Role.Disabled, capturedUser.role)
        assertEquals("Encoded password", capturedUser.password)
        assertEquals(capturedUser.organization, "organization")
    }

    @Test
    fun `registerUser fails if there is already a user with the same email in the database`() {
        val repository = getRepository(true)
        val service = UserService(repository, getPasswordEncoder())

        every { repository.save(any()) }.throws(DataIntegrityViolationException("Duplicate email"))

        val testUser2 = User("username", "email", Role.Disabled, "password")
        assertThrows<ForbiddenOperationException> { service.registerUser(testUser2, "organization") }
    }

    @Test
    fun `changeRole does not fail when a user with id exists and admin changes the role`() {
        val repository = getRepository(true)
        val otherAdmin = User("Other admin", "otherAdmin@email.com", Role.Admin, "password", testOrganization)
        every { repository.findByOrganizationAndRole(testOrganization, Role.Admin) } returns listOf(testUser, otherAdmin)
        val service = UserService(repository, getPasswordEncoder())
        service.changeRole(testId, Role.Coach)
        verify { repository.save(testUser) }
        assertEquals(Role.Coach, testUser.role)
        service.changeRole(testId, Role.Admin)
    }

    @Test
    fun `changeRole fails when demoting the last admin`() {
        val repository = getRepository(true)
        every { repository.findByOrganizationAndRole(testOrganization, Role.Admin) } returns listOf(testUser)
        val service = UserService(repository, getPasswordEncoder())
        assertThrows<ForbiddenOperationException> { service.changeRole(testId, Role.Coach) }
    }

    @Test
    fun `changeRole fails when no user with id exists`() {
        val service = UserService(getRepository(false), getPasswordEncoder())
        assertThrows<InvalidIdException> { service.changeRole(testId, Role.Coach) }
    }

    @Test
    fun `changeRole does not fail even when changing to same role`() {
        val repository = getRepository(true)
        val testCoachUser = User("Coach", "coach@email.com", Role.Coach, "password")
        val testCoachId = testCoachUser.id
        every { repository.findByIdOrNull(testCoachId) } returns testCoachUser
        every { repository.save(testCoachUser) } returns testCoachUser
        val service = UserService(repository, getPasswordEncoder())
        service.changeRole(testCoachId, Role.Disabled)
        verify { repository.save(testCoachUser) }
        assertEquals(Role.Disabled, testCoachUser.role)
        service.changeRole(testCoachId, Role.Admin)
        verify { repository.save(testCoachUser) }
        assertEquals(Role.Admin, testCoachUser.role)
        service.changeRole(testCoachId, Role.Admin)
        verify { repository.save(testCoachUser) }
        assertEquals(Role.Admin, testCoachUser.role)
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
