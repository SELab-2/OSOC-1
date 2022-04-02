package be.osoc.team1.backend.entities

import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

/**
 * Represents the requirement of specific roles for a [Project].
 * To be given this role you need a certain [skill] and the amount of people that can be assigned to this role is limited
 * by the [amount] field.
 */
@Entity
class RoleRequirement(
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    val skill: Skill,
    val amount: Int
) {
    @Id
    val id: UUID = UUID.randomUUID()
}

/**
 * Represents an assignment of a [student] on a [Project]. The assignment was suggested by [suggester] with a [reason].
 * The assignment assigns [student] to a specific role specified by [RoleRequirement].
 */
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
 * Represents a project in the database. A project is constructed with a [name], a [clientName]
 * and a [description]. Note that neither of these fields, nor the combination of both of them need be unique.
 * A project also has [coaches], which is a list of coaches who will be aiding with this project.
 * A project also has [requiredRoles] these are roles that will have to be filled by students.
 * The assignment of these students to this project is represented by [assignments].
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
