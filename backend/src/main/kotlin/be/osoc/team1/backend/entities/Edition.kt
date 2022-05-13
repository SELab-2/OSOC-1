package be.osoc.team1.backend.entities

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Represents an edition of OSOC, identified by the given [name]. An edition can be active or inactive,
 * this is described by the [isActive] field. Because we're currently restricting the amount of active editions
 * to one, there should only be at most one edition in the database with [isActive] set to true.
 */
@Entity
class Edition(@Id val name: String, var isActive: Boolean) {
    // Needed for tests
    override fun equals(other: Any?): Boolean =
        other is Edition && name == other.name && isActive == other.isActive

    fun accessibleBy(user: User): Boolean = this.isActive || user.role.hasPermissionLevel(Role.Admin)
}
