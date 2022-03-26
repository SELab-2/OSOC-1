package be.osoc.team1.backend.entities

import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToMany
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
 * A project also has [students], which is a list of the students assigned to this project
 * Finally, a project also has [coaches], which is a list of coaches who will be aiding with this project
 */
@Entity
class Project(
    val name: String,

    val clientName: String,

    val description: String,

    @ManyToMany(cascade = [CascadeType.ALL])
    val students: MutableCollection<Student> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL])
    val coaches: MutableCollection<User> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val requiredRoles: List<RoleRequirement> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val assignments: MutableCollection<Assignment> = mutableListOf()
) {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID()
}
