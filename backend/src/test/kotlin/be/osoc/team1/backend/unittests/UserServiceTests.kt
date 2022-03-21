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
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

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

    @Test
    fun `getAllUsers does not fail`() {
        val service = UserService(getRepository(true))
        assertEquals(service.getAllUsers(), listOf(testUser))
    }

    @Test
    fun `getUserById does not fail when the user with id exists`() {
        val service = UserService(getRepository(true))
        assertEquals(service.getUserById(testId), testUser)
    }

    @Test
    fun `getUserById fails when the user with id does not exist`() {
        val service = UserService(getRepository(false))
        assertThrows<InvalidIdException> { service.getUserById(testId) }
    }

    @Test
    fun `deleteUserById does not fail when the user with id exists`() {
        val repository = getRepository(true)
        val service = UserService(repository)
        service.deleteUserById(testId)
        verify { repository.deleteById(testId) }
    }

    @Test
    fun `deleteUserById fails when the user with id does not exist`() {
        val service = UserService(getRepository(false))
        assertThrows<InvalidIdException> { service.deleteUserById(testId) }
    }

    @Test
    fun `postUser does not fail`() {
        val repository = getRepository(true)
        val service = UserService(repository)
        service.postUser(testUser)
        verify { repository.save(testUser) }
    }

    @Test
    fun `changeRole does not fail when a user with id exists and admin changes the role`() {
        val repository = getRepository(true)
        val otherAdmin = User("Other admin", "otherAdmin@email.com", Role.Admin, "password")
        every { repository.findByRole(Role.Admin) } returns listOf(testUser, otherAdmin)
        val service = UserService(repository)
        service.changeRole(testId, Role.Coach)
        verify { repository.save(testUser) }
        assertEquals(testUser.role, Role.Coach)
        service.changeRole(testId, Role.Admin)
    }

    @Test
    fun `changeRole fails when demoting the last admin`() {
        val repository = getRepository(true)
        every { repository.findByRole(Role.Admin) } returns listOf(testUser)
        val service = UserService(repository)
        assertThrows<ForbiddenOperationException> { service.changeRole(testId, Role.Coach) }
    }

    @Test
    fun `changeRole fails when no user with id exists`() {
        val service = UserService(getRepository(false))
        assertThrows<InvalidIdException> { service.changeRole(testId, Role.Coach) }
    }

    @Test
    fun `changeRole does not fail even when changing to same role`() {
        val repository = getRepository(true)
        val testCoachUser = User("Coach", "coach@email.com", Role.Coach, "password")
        val testCoachId = testCoachUser.id
        every { repository.findByIdOrNull(testCoachId) } returns testCoachUser
        every { repository.save(testCoachUser) } returns testCoachUser
        val service = UserService(repository)
        service.changeRole(testCoachId, Role.Disabled)
        verify { repository.save(testCoachUser) }
        assertEquals(testCoachUser.role, Role.Disabled)
        service.changeRole(testCoachId, Role.Admin)
        verify { repository.save(testCoachUser) }
        assertEquals(testCoachUser.role, Role.Admin)
        service.changeRole(testCoachId, Role.Admin)
        verify { repository.save(testCoachUser) }
        assertEquals(testCoachUser.role, Role.Admin)
    }

    @Test
    fun `patchUser does not fail when a user with id exists`() {
        val repository = getRepository(true)
        val service = UserService(repository)
        service.patchUser(testUser)
        verify { repository.save(testUser) }
    }

    @Test
    fun `patchUser fails when no user with id exists`() {
        val service = UserService(getRepository(false))
        assertThrows<InvalidIdException> { service.patchUser(testUser) }
    }
}
