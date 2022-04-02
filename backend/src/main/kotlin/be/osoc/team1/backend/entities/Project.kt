package be.osoc.team1.backend.entities

import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity
class RoleRequirement(
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    val skill: Skill,
    val amount: Int
) {
    @Id
    val id: UUID = UUID.randomUUID()
}

@Entity
class Assignment(
    @OneToOne
    val student: Student,
    @OneToOne
    val roleRequirement: RoleRequirement,
    @OneToOne
    val suggester: User,
    val reason: String
) {
    @Id
    val id: UUID = UUID.randomUUID()
}

/**
 * Represents a project in the database. A project is constructed with a [name]
 * and a [description]. Note that neither of these fields, nor the combination of both of them need be unique.
 * Finally, a project also has [coaches], which is a list of coaches who will be aiding with this project
 */
@Entity
class Project(
    val name: String,

    val clientName: String,

    val description: String,

    @OneToMany(cascade = [CascadeType.ALL])
    val coaches: MutableCollection<User> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val requiredRoles: List<RoleRequirement> = listOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val assignments: MutableCollection<Assignment> = mutableListOf()
) {
    @Id
    val id: UUID = UUID.randomUUID()
}
