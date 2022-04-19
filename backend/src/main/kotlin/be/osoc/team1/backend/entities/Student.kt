package be.osoc.team1.backend.entities

import be.osoc.team1.backend.services.nameMatchesSearchQuery
import be.osoc.team1.backend.util.AnswerListSerializer
import be.osoc.team1.backend.util.CommunicationListSerializer
import be.osoc.team1.backend.util.StatusSuggestionListSerializer
import be.osoc.team1.backend.util.TallyDeserializer
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
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
 * Every [StatusSuggestion] is made by a coach of type [User]. The [coachId] of the [User] who made the suggestion
 * is included in the object. A coach can make multiple suggestions about different [Student]s, but
 * it wouldn't make any sense for a coach to make multiple suggestions about the same [Student].
 * Therefore the combination of [coachId] and [student] must be unique.
 * This constraint is checked when adding a new [StatusSuggestion] to a [Student].
 * A [StatusSuggestion] always belongs to one particular [Student] in the database.
 * This [Student] is included in the [StatusSuggestion] to verify the unique constraint,
 * but when serializing the [StatusSuggestion] to a JSON object, this field is ignored.
 * This is because the only way to get [StatusSuggestion]s is to GET a [Student] and read
 * it's [Student.statusSuggestions] field. Therefore, it's already clear which [Student] is being referred to.
 * Apart from the suggested [status], a [motivation] for the suggestion should also be included.
 */
@Entity
class StatusSuggestion(val coachId: UUID, val status: SuggestionEnum, val motivation: String) {

    @Id
    val id: UUID = UUID.randomUUID()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonIgnore
    // Needs to be a nullable var for synchronization purposes. See
    // https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/
    // (Bidirectional @OneToMany) for more information. I didn't implement the most efficient option
    // in this article, because I couldn't get the code to work when the mappedBy attribute was added
    // to the @OneToMany side of the relation.
    var student: Student? = null
}

/**
 * An [Answer] object stores an answer to a [question]. Because these questions sometimes have multiple answers, for
 * example when you can select multiple options, the [answer] is stored as a list of strings. In the case of
 * MULTIPLE_CHOICE questions in which there are multiple options and one answer an optionId will be stored. This is used
 * for the alumni question. We use the id instead of just comparing the string with the hope that if the answer were to
 * change slightly in the form the id would still remain the same, and we wouldn't have to update the code.
 */
@Entity
class Answer(
    val key: String,
    val question: String,
    @ElementCollection
    val answer: Collection<String>,
    @JsonIgnore
    val optionId: String = ""
) {
    @Id
    val id: UUID = UUID.randomUUID()
}

/**
 * Represents a student in the database. A student is constructed with a [firstName]
 * and a [lastName]. Note that neither of these fields, nor the combination of both of them need be unique.
 * A student also has a set of [skills].
 * I.e. there could be two students in the database with [firstName] "Tom" and [lastName] "Alard".
 * A student also has a [status], see the documentation of [StatusEnum] for more information on what it represents.
 * Whether a student is an alumn can be indicated by the [alumn] boolean (default false).
 * A student belongs to a particular [edition] of OSOC.
 * Finally, each student keeps a [MutableList] of [StatusSuggestion]s.
 */
@Entity
@JsonDeserialize(using = TallyDeserializer::class)
class Student(
    val firstName: String,
    val lastName: String,

    @JsonIgnore
    val edition: String,

    @ManyToMany(cascade = [CascadeType.ALL])
    val skills: Set<Skill> = sortedSetOf(),
    val alumn: Boolean = false,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonSerialize(using = AnswerListSerializer::class)
    val answers: List<Answer> = listOf()
) {

    @Id
    val id: UUID = UUID.randomUUID()

    var status: StatusEnum = StatusEnum.Undecided

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonSerialize(using = StatusSuggestionListSerializer::class)
    val statusSuggestions: MutableList<StatusSuggestion> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonSerialize(using = CommunicationListSerializer::class)
    val communications: MutableList<Communication> = mutableListOf()
}

/**
 * This function will filter [Student]s based on given [statuses]
 * Only [Student]s who have 1 of the given [statuses] will be kept
 */
fun List<Student>.filterByStatus(statuses: List<StatusEnum>) =
    filter { student: Student -> statuses.contains(student.status) }

/**
 * This function will filter [Student]s based on given [nameQuery]
 * Only [Student]s whose preprocessed name matches the preprocessed [nameQuery] will be kept (see [nameMatchesSearchQuery])
 */
fun List<Student>.filterByName(nameQuery: String) =
    filter { student: Student -> nameMatchesSearchQuery("${student.firstName} ${student.lastName}", nameQuery) }

/**
 * This function will filter [Student]s based on given [includeSuggested] boolean
 * if [includeSuggested] is false only [Student] for which the [callee] hasn't made a suggestion yet will be returned
 * else return all of the [Student]s
 */
fun List<Student>.filterBySuggested(includeSuggested: Boolean, callee: User) =
    filter { student: Student -> includeSuggested || student.statusSuggestions.none { it.coachId == callee.id } }
