package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val repository: UserRepository) {

    fun getAllUsers(): Iterable<User> = repository.findAll()

    /**
     * Get a user using their [id]. Throws an [InvalidIdException] if no such user exists.
     */
    fun getUserById(id: UUID) = repository.findByIdOrNull(id) ?: throw InvalidIdException()

    /**
     * Delete the user with this [id]. Throws an [InvalidIdException] if such user exists.
     */
    fun deleteUserById(id: UUID) {
        if (!repository.existsById(id))
            throw InvalidIdException()

        repository.deleteById(id)
    }

    /**
     * Save [user] in the [repository]. Returns the id of the newly saved user object.
     */
    fun putUser(user: User) = repository.save(user).id

    /**
     * Change the role of the user with this [id] to [newRole]. If this user does not exist an [InvalidIdException] will
     * be thrown.
     */
    fun changeRole(id: UUID, newRole: Role) {
        val user = getUserById(id)
        user.role = newRole
        repository.save(user)
    }

    /**
     * Update a user object with the data defined in [updatedUser]. If the user we are trying to update doesn't exist
     * then we will throw an [InvalidIdException].
     */
    fun patchUser(updatedUser: User) {
        if (!repository.existsById(updatedUser.id))
            throw InvalidIdException()

        repository.save(updatedUser)
    }
}
