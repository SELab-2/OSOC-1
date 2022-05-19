package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidForgotPasswordUUIDException
import be.osoc.team1.backend.repositories.UserRepository
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
        val userRepository: UserRepository = mockk()
        every { userRepository.findByEmail(testEmail) } returns if (userAlreadyExists) testUser else null
        every { userRepository.save(testUser) } returns testUser
        return userRepository
    }

    @Test
    fun `sendEmailWithToken does not fail when email is valid`() {
        val forgotPasswordService = ForgotPasswordService(getRepository(), mockk())
        forgotPasswordService.sendEmailWithToken(testEmail)
    }

    @Test
    fun `sendEmailWithToken does not fail when email is invalid`() {
        val forgotPasswordService = ForgotPasswordService(getRepository(false), mockk())
        forgotPasswordService.sendEmailWithToken(testEmail)
    }

    @Test
    fun `changePassword fails when forgotPasswordUUID is invalid`() {
        val invalidUUID = UUID.randomUUID()
        val forgotPasswordService = ForgotPasswordService(getRepository(), mockk())
        val exception = Assertions.assertThrows(InvalidForgotPasswordUUIDException().javaClass) {
            forgotPasswordService.changePassword(invalidUUID, newPassword)
        }
        Assertions.assertEquals("forgotPasswordUUID is invalid.", exception.message)
    }

    @Test
    fun `changePassword fails when email is invalid`() {
        val forgotPasswordService = ForgotPasswordService(getRepository(false), mockk())
        val validUUID = forgotPasswordService.newToken(testEmail)
        val exception = Assertions.assertThrows(InvalidForgotPasswordUUIDException().javaClass) {
            forgotPasswordService.changePassword(validUUID, newPassword)
        }
        Assertions.assertEquals("ForgotPasswordToken contains invalid email.", exception.message)
    }

    @Test
    fun `changePassword does not fail when valid arguments given`() {
        val passwordEncoder: PasswordEncoder = mockk()
        every { passwordEncoder.encode(any()) } returns "Encoded password"
        val forgotPasswordService = ForgotPasswordService(getRepository(), passwordEncoder)
        val validUUID = forgotPasswordService.newToken(testEmail)
        forgotPasswordService.changePassword(validUUID, newPassword)
    }
}
