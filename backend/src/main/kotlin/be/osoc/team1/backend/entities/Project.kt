package be.osoc.team1.backend.entities

import be.osoc.team1.backend.util.StudentSerializer
import be.osoc.team1.backend.util.UserListSerializer
import be.osoc.team1.backend.util.UserSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

/**
 * Represents the requirement of specific positions for a [Project].
 * To be given this position you need a certain [skill] and the amount of people that can be assigned to this position
 * is limited by the [amount] field.
 */
@Entity
class Position(
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    val skill: Skill,
    val amount: Int
) {
    @Id
    val id: UUID = UUID.randomUUID()
}

/**
 * Represents an assignment of a [student] on a [Project]. The assignment was suggested by [suggester] with a [reason].
 * The assignment assigns [student] to a specific [position].
 */
@Entity
class Assignment(
    @OneToOne
    @JsonSerialize(using = StudentSerializer::class)
    val student: Student,

    @OneToOne
    val position: Position,

    @OneToOne
    @JsonSerialize(using = UserSerializer::class)
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
 * A project also has [positions] that will have to be filled by students.
 * The assignment of students to these positions on the project is represented by [assignments].
 */
@Entity
class Project(
    val name: String,

    val clientName: String,

    val description: String,

    @OneToMany(cascade = [CascadeType.ALL])
    @JsonSerialize(using = UserListSerializer::class)
    val coaches: MutableCollection<User> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val positions: Collection<Position> = listOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val assignments: MutableCollection<Assignment> = mutableListOf()
) {
    @Id
    val id: UUID = UUID.randomUUID()
}
