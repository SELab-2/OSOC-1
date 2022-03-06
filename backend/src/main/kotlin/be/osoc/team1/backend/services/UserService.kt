package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val repository: UserRepository) {

    fun getAllUsers(): Iterable<User> = repository.findAll()

    fun getUserById(id: UUID) = repository.findById(id)

    fun deleteUserById(id: UUID) {
        if (!repository.existsById(id))
            throw InvalidIdException()

        repository.deleteById(id)
    }

    fun putUser(user: User) = repository.save(user).id

    fun changeRole(id: UUID, newRole: Role) {
        val userOptional = repository.findById(id)
        if (userOptional.isEmpty)
            throw InvalidIdException()

        val user = userOptional.get()
        user.role = newRole
        repository.save(user)
    }

    fun patchUser(updatedUser: User) {
        if (!repository.existsById(updatedUser.id))
            throw InvalidIdException()

        repository.save(updatedUser)
    }
}
