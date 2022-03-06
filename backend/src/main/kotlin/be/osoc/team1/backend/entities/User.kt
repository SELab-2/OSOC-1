package be.osoc.team1.backend.entities

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

enum class Role(private val permissionLevel: Int) {
    Admin(3),
    Coach(2),
    StudentCoach(1),
    Disabled(0);

    fun hasPermissionLevel(role: Role) : Boolean {
        return permissionLevel >= role.permissionLevel
    }
}

@Entity
@Table(name = "account")
class User(
    val name: String,
    val email: String,
    var role: Role,
    val password: String
) {
    @Id
    @GeneratedValue(generator = "UUID")
    val id: UUID = UUID.randomUUID()
}
