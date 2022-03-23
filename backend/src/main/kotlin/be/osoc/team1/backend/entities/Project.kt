package be.osoc.team1.backend.entities

import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidUserIdException
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
    val amount: Int,
    @ManyToMany(cascade = [CascadeType.ALL])
    val assignees: MutableSet<Student> = mutableSetOf()
) {
    @Id
    val id: UUID = UUID.randomUUID()

    fun assign(student: Student) {
        if (assignees.size >= amount)
            throw ForbiddenOperationException("This role already has enough assignees!")

        if (!student.skills.contains(skill))
            throw ForbiddenOperationException("This student doesn't have the required skill to be assigned this role.")

        assignees.add(student)
    }

    fun remove(student: Student) {
        if (!assignees.remove(student))
            throw InvalidUserIdException("The specified user is not assigned to this role!")
    }
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
    val requiredRoles: List<RoleRequirement> = mutableListOf()
) {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID()
}
