package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidUserIdException
import be.osoc.team1.backend.repositories.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val repository: UserRepository) {

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
     * Save [user] in the [repository]. Returns the id of the newly saved user object.
     */
    fun postUser(user: User): UUID {
        try {
            return repository.save(user).id
        } catch (dbe: DataIntegrityViolationException) {
            throw ForbiddenOperationException("User creation failed due to a DataIntegrityViolationException!")
        }
    }

    /**
     * Change the role of the user with this [id] to [newRole]. If this user does not exist an [InvalidUserIdException]
     * will be thrown. If the role of the last remaining admin is changed to a role with fewer permissions a
     * [ForbiddenOperationException] will be thrown. Currently, anyone can change the role of any other user, this will
     * be changed when we have functional authentication in place.
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
     * Update a user object with the data defined in [updatedUser]. If this user does not exist an
     * [InvalidUserIdException] will be thrown.
     */
    fun patchUser(updatedUser: User) {
        if (!repository.existsById(updatedUser.id))
            throw InvalidUserIdException()

        repository.save(updatedUser)
    }
}
