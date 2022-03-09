package be.osoc.team1.backend.entities

import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * Represents the possible values that a student's status can have.
 *
 * Yes: Student looks promising and we have a project for them.
 *
 * Maybe: Student looks promising but we might not have a project for them.
 *
 * No: Student doesn't look promising.
 *
 * Undecided: No decision has been made yet about this student.
 *
 */
enum class StatusEnum {
    Yes, Maybe, No, Undecided
}

/**
 * Represents the possible values a suggestion to update a student's status can have.
 *
 * Yes: Student looks promising and we have a project for them.
 *
 * Maybe: Student looks promising but we might not have a project for them.
 *
 * No: Student doesn't look promising.
 *
 * Importantly, the Undecided value is not included in this enum. This is because
 * it's not allowed to suggest to change a student's status to Undecided.
 */
enum class SuggestionEnum {
    Yes, Maybe, No
}

/**
 * Represents the entry of a [status] suggestion in the database.
 * Apart from the suggested [status], a [motivation] for the suggestion should also be included.
 * A [StatusSuggestion] always belongs to one particular [Student] in the database, but this [Student]
 * is not stored in the [StatusSuggestion], as this is a unidirectional relation.
 */
@Entity
class StatusSuggestion(val status: SuggestionEnum, val motivation: String) {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID()
}

/**
 * Represents a student in the database. A student is constructed with a [firstName]
 * and a [lastName]. Note that neither of these fields, nor the combination of both of them need be unique.
 * I.e. there could be two students in the database with [firstName] "Tom" and [lastName] "Alard".
 * A student also has a [status], see the documentation of [StatusEnum] for more information on what it represents.
 * Finally, each student keeps a [MutableList] of [StatusSuggestion]s.
 */
@Entity
class Student(val firstName: String, val lastName: String) {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID()

    var status: StatusEnum = StatusEnum.Undecided

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusSuggestions: MutableList<StatusSuggestion> = mutableListOf()
}
