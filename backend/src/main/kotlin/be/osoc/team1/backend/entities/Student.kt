package be.osoc.team1.backend.entities

import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.services.nameMatchesSearchQuery
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
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.validation.constraints.NotBlank

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
 * Therefore the combination of [coachId] and [Student] must be unique.
 * This constraint is checked when adding a new [StatusSuggestion] to a [Student].
 * A [StatusSuggestion] always belongs to one particular [Student] in the database.
 * This [Student] is included in the [StatusSuggestion] to verify the unique constraint,
 * but when serializing the [StatusSuggestion] to a JSON object, this field is ignored.
 * This is because the only way to get [StatusSuggestion]s is to GET a [Student] and read
 * it's [Student.statusSuggestions] field. Therefore, it's already clear which [Student] is being referred to.
 * Apart from the suggested [status], a [motivation] for the suggestion should also be included.
 */
@Entity
class StatusSuggestion(
    val coachId: UUID,
    val status: SuggestionEnum,
    val motivation: String,
    @JsonIgnore
    @NotBlank
    val edition: String = ""
) {

    @Id
    val id: UUID = UUID.randomUUID()
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
    val optionId: String = "",
    @JsonIgnore
    val studentId: UUID
) {
    @Id
    val id: UUID = UUID.randomUUID()

    @JsonIgnore
    @NotBlank
    lateinit var edition: String
}

// @Entity
class AnswerRegister(
    val key: String,
    val question: String,
    @ElementCollection
    val answer: Collection<String>,
    @JsonIgnore
    val optionId: String = "",
) {
    @Id
    val id: UUID = UUID.randomUUID()

    @JsonIgnore
    @NotBlank
    lateinit var edition: String
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
// @JsonDeserialize(using = TallyDeserializer::class)
class Student(
    val firstName: String,
    val lastName: String,

    @JsonIgnore
    @NotBlank
    val edition: String = "",

    @ManyToMany(cascade = [CascadeType.MERGE])
    @OrderBy
    val skills: Set<Skill> = sortedSetOf(),
    val alumn: Boolean = false,
    val possibleStudentCoach: Boolean = false,
    // @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    // @JsonSerialize(using = AnswerListSerializer::class)
    // val answers: List<Answer> = listOf()
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

// @Entity
@JsonDeserialize(using = TallyDeserializer::class)
class StudentRegister(
    val firstName: String,
    val lastName: String,

    @JsonIgnore
    @NotBlank
    val edition: String = "",

    @ManyToMany(cascade = [CascadeType.MERGE])
    @OrderBy
    val skills: Set<Skill> = sortedSetOf(),
    val alumn: Boolean = false,
    val possibleStudentCoach: Boolean = false,
    val answers: List<AnswerRegister> = listOf()
) {

    @Id
    val id: UUID = UUID.randomUUID()

    var status: StatusEnum = StatusEnum.Undecided

    // @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    // @JsonSerialize(using = StatusSuggestionListSerializer::class)
    // val statusSuggestions: MutableList<StatusSuggestion> = mutableListOf()

    // @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    // @JsonSerialize(using = CommunicationListSerializer::class)
    // val communications: MutableList<Communication> = mutableListOf()
}
/**
 * This function will filter [Student]s based on given [statuses]
 * Only [Student]s who have 1 of the given [statuses] will be kept
 */
fun List<Student>.filterByStatus(statuses: Set<StatusEnum>) =
    filter { student: Student -> statuses.contains(student.status) }

/**
 * This function will filter [Student]s based on given [nameQuery]
 * Only [Student]s whose preprocessed name matches the preprocessed [nameQuery] will be kept (see [nameMatchesSearchQuery])
 */
fun List<Student>.filterByName(nameQuery: String) =
    filter { student: Student -> nameMatchesSearchQuery("${student.firstName} ${student.lastName}", nameQuery) }

/**
 * This function will filter [Student]s so that only [Student]s for which the [callee] hasn't made a suggestion yet will
 * be returned.
 */
fun List<Student>.filterBySuggested(callee: User) =
    filter { student: Student -> student.statusSuggestions.none { it.coachId == callee.id } }

/**
 * This function will filter [Student]s based on a set of [skillNames].
 * Only students that have at least one of these skills will be returned.
 */
fun List<Student>.filterBySkills(skillNames: Set<String>) =
    filter { student -> student.skills.map { it.skillName }.intersect(skillNames).isNotEmpty() }

/**
 * This function will filter a list of [Student]s to only return alumni.
 */
fun List<Student>.filterByAlumn() = filter { it.alumn }

/**
 * This function will filter a list of [Student]s to only return students that would like to be a student coach.
 */
fun List<Student>.filterByStudentCoach() = filter { it.possibleStudentCoach }

/**
 * This function will filter a list of [Student]s to only return students who have not yet been assigned to a [Project].
 */
fun List<Student>.filterByNotYetAssigned(assignmentRepository: AssignmentRepository) =
    this.toSet().subtract(assignmentRepository.findAll().map { it.student }.toSet()).toList()
