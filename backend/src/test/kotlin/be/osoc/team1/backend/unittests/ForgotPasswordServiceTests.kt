package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidForgotPasswordUUIDException
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.security.ForgotPasswordUtil
import be.osoc.team1.backend.services.ForgotPasswordService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

class ForgotPasswordServiceTests {
    private val testEmail = "test@email.com"
    private val testUser = User("Test", testEmail, Role.Admin, "password")
    private val newPassword = "new_password"

    private fun getRepository(userAlreadyExists: Boolean = true): UserRepository {
        val repository: UserRepository = mockk()
        every { repository.findByEmail(testEmail) } returns if (userAlreadyExists) testUser else null
        every { repository.save(testUser) } returns testUser
        return repository
    }

    @Test
    fun `sendEmailWithToken does not fail when email is valid`() {
        val service = ForgotPasswordService(getRepository(), mockk())
        service.sendEmailWithToken(testEmail)
    }

    @Test
    fun `sendEmailWithToken does not fail when email is invalid`() {
        val service = ForgotPasswordService(getRepository(false), mockk())
        service.sendEmailWithToken(testEmail)
    }

    @Test
    fun `changePassword fails when forgotPasswordUUID is invalid`() {
        val invalidUUID = UUID.randomUUID()
        val service = ForgotPasswordService(getRepository(), mockk())
        val exception = Assertions.assertThrows(InvalidForgotPasswordUUIDException().javaClass) {
            service.changePassword(invalidUUID, newPassword)
        }
        Assertions.assertEquals("forgotPasswordUUID is invalid.", exception.message)
    }

    @Test
    fun `changePassword fails when email is invalid`() {
        val validUUID = ForgotPasswordUtil.newToken(testEmail)
        val service = ForgotPasswordService(getRepository(false), mockk())
        val exception = Assertions.assertThrows(InvalidForgotPasswordUUIDException().javaClass) {
            service.changePassword(validUUID, newPassword)
        }
        Assertions.assertEquals("ForgotPasswordToken contains invalid email.", exception.message)
    }

    @Test
    fun `changePassword does not fail when valid arguments given`() {
        val validUUID = ForgotPasswordUtil.newToken(testEmail)
        val passwordEncoder: PasswordEncoder = mockk()
        every { passwordEncoder.encode(any()) } returns "Encoded password"
        val service = ForgotPasswordService(getRepository(), passwordEncoder)
        service.changePassword(validUUID, newPassword)
    }
}
