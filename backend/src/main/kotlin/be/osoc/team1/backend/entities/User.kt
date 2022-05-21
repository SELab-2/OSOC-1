package be.osoc.team1.backend.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
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
 * hashed value of the password. Unlike some other entities, [User]s are not associated with a specific edition. See the comment
 * [here](https://github.com/SELab-2/OSOC-1/pull/170#issue-1187899278) for more information on why this is the case.
 */
@Entity
@Table(name = "account")
class User(
    @field:JsonView(EntityViews.Public::class)
    val username: String,

    @Column(unique = true)
    @field:JsonView(EntityViews.Public::class)
    val email: String,

    @field:JsonView(EntityViews.Public::class)
    var role: Role = Role.Disabled,

    @field:JsonView(EntityViews.Hidden::class)
    var password: String,
) {
    @ManyToMany(cascade = [CascadeType.DETACH])
    @JsonIgnore
    @JoinTable(
        name = "project_coaches",
        joinColumns = [JoinColumn(name = "coaches_id")],
        inverseJoinColumns = [JoinColumn(name = "project_id")]
    )
    val projects: MutableList<Project> = mutableListOf()

    @OneToMany(mappedBy = "suggester", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    val statusSuggestions: MutableList<StatusSuggestion> = mutableListOf()

    @Id
    @field:JsonView(EntityViews.Public::class)
    val id: UUID = UUID.randomUUID()
}
