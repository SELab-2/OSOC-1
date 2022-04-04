package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface UserRepository : CrudRepository<User, UUID> {
    fun findByRole(role: Role): Collection<User>
    fun findByEmail(email: String): User?
}
