package be.osoc.team1.backend.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * The [Role] enum contains an integer allowing us to define permission levels and compare the security permissions
 * of different [Role] enum types. Every role has permission to use everything with an equal or below [permissionLevel].
 */
enum class Role(private val permissionLevel: Int) {
    Admin(2),
    Coach(1),
    Disabled(0);

    /**
     * Check if [role] has a [permissionLevel] of at minimum another role, return true if that's the case.
     */
    fun hasPermissionLevel(role: Role): Boolean {
        return permissionLevel >= role.permissionLevel
    }
}

/**
 * [User] object containing the [username] of the user, their [email], the [role] of the user which uses an enum class [Role]
 * and a [password]. This password is of type [String] but doesn't have to be the plain text password, it could be the
 * hashed value of the password.
 */
@Entity
@Table(name = "account")
class User(
    val username: String,
    val email: String,
    var role: Role,
    val password: String
) {
    @Id
    val id: UUID = UUID.randomUUID()
}
