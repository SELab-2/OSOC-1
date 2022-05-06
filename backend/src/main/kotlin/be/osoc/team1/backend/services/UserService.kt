package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidTokenException
import be.osoc.team1.backend.exceptions.InvalidUserIdException
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.security.EmailUtil
import be.osoc.team1.backend.security.ResetPasswordUtil
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val repository: UserRepository, private val passwordEncoder: PasswordEncoder) {

    fun getAllUsers(): Iterable<User> = repository.findAll()

    /**
     * Get a user using their [id]. Throws an [InvalidUserIdException] if no such user exists.
     */
    fun getUserById(id: UUID) = repository.findByIdOrNull(id) ?: throw InvalidUserIdException()

    /**
     * Delete the user with this [id]. Throws an [InvalidUserIdException] if no such user exists.
     */
    fun deleteUserById(id: UUID) {
        if (!repository.existsById(id))
            throw InvalidUserIdException()

        repository.deleteById(id)
    }

    /**
     * Register a new [User] in the [repository]. Returns the newly created user object. A
     * [ForbiddenOperationException] will be thrown if a constraint on the user is violated. This should only happen if
     * there already exists another user with the specified email address. The newly created user will have the
     * [Role.Disabled] role by default.
     */
    fun registerUser(plaintextPasswordUser: User): User {
        val encodedPassword = passwordEncoder.encode(plaintextPasswordUser.password)
        val encodedPasswordUser = User(
            plaintextPasswordUser.username,
            plaintextPasswordUser.email,
            Role.Disabled,
            encodedPassword
        )
        try {
            return repository.save(encodedPasswordUser)
        } catch (_: DataIntegrityViolationException) {
            throw ForbiddenOperationException("User with email = '${encodedPasswordUser.email}' already exists!")
        }
    }

    /**
     * Change the role of the user with this [id] to [newRole]. If this user does not exist an [InvalidUserIdException]
     * will be thrown. If the role of the last remaining admin is changed to a role with fewer permissions a
     * [ForbiddenOperationException] will be thrown. Currently, anyone can change the role of
     * any other user, this will be changed when we have functional authentication in place.
     */
    fun changeRole(id: UUID, newRole: Role) {
        val user = getUserById(id)
        if (user.role == Role.Admin && !newRole.hasPermissionLevel(Role.Admin) &&
            repository.findByRole(Role.Admin).size == 1
        ) {
            throw ForbiddenOperationException("Cannot demote last remaining admin")
        }
        user.role = newRole
        repository.save(user)
    }

    /**
     * Update a user object with the data defined in [updatedUser].
     * If this user does not exist an [InvalidUserIdException] will be thrown.
     * If password gets changed a [ForbiddenOperationException] will be thrown.
     */
    fun patchUser(updatedUser: User): User {
        val oldUser = getUserById(updatedUser.id)
        if (oldUser.password != updatedUser.password) {
            throw ForbiddenOperationException("Not allowed to update password field of users")
        }
        return repository.save(updatedUser)
    }

    /**
     * Send an email with a resetPasswordToken to [email] if [email] is the email address of an existing user.
     */
    fun sendEmailWithToken(emailAddress: String) {
        if (repository.findByEmail(emailAddress) != null) {
            val resetPasswordUUID: UUID = ResetPasswordUtil.newToken(emailAddress)
            EmailUtil.sendEmail(emailAddress, resetPasswordUUID)
        }
    }

    /**
     * Get the email address from [resetPasswordUUID] and set its password to [newPassword].
     */
    fun changePassword(resetPasswordUUID: UUID, newPassword: String) {
        val emailAddress = ResetPasswordUtil.getEmailFromUUID(resetPasswordUUID)
            ?: throw InvalidTokenException("resetPasswordUUID is invalid.")
        val user: User = repository.findByEmail(emailAddress)
            ?: throw InvalidTokenException("ResetPasswordToken contains invalid email.")
        user.password = passwordEncoder.encode(newPassword)
        repository.save(user)
    }
}
