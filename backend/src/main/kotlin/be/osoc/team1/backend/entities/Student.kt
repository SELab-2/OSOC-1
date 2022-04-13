package be.osoc.team1.backend.entities

import be.osoc.team1.backend.exceptions.FailedOperationException
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
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
    @JsonIgnore
    // Only used as a primary key, ignored otherwise.
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
 * Represents a student in the database. A student is constructed with a [firstName]
 * and a [lastName]. Note that neither of these fields, nor the combination of both of them need be unique.
 * A student also has a set of [skills].
 * I.e. there could be two students in the database with [firstName] "Tom" and [lastName] "Alard".
 * A student also has a [status], see the documentation of [StatusEnum] for more information on what it represents.
 * Whether a student is an alumn can be indicated by the [alumn] boolean (default false).
 * Finally, each student keeps a [MutableList] of [StatusSuggestion]s.
 */
@Entity
@JsonDeserialize(using = TallyDeserializer::class)
class Student(
    val firstName: String,
    val lastName: String,
    @ManyToMany(cascade = [CascadeType.ALL])
    val skills: Set<Skill> = sortedSetOf(),
    val alumn: Boolean = false,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val answers: List<Answer> = listOf()
) {

    @Id
    val id: UUID = UUID.randomUUID()

    var status: StatusEnum = StatusEnum.Undecided

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusSuggestions: MutableList<StatusSuggestion> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val communications: MutableList<Communication> = mutableListOf()
}

class TallyDeserializer : StdDeserializer<Student>(Student::class.java) {
    private val nameQuestionKey = "question_nroEGL"
    private val lastnameQuestionKey = "question_w4KjAo"
    private val alumnQuestionKey = "question_wz7eGE"
    private val alumnYesId = "689451da-305b-451a-8039-c748ff06ec82"
    private val skillQuestionKey = "question_3X4q1V"

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Student {
        val node : JsonNode = parser.codec.readTree(parser)
        val fields = node.get("data").get("fields").toList()
        val answers = fields.map { processQuestionNode(it) }

        val firstnameAnswer = answers.find { it.key == nameQuestionKey }
            ?: throw FailedOperationException("Could not find firstname question!")
        val lastnameAnswer = answers.find { it.key == lastnameQuestionKey }
            ?: throw FailedOperationException("Could not find lastname question!")
        val alumnAnswer = answers.find { it.key == alumnQuestionKey }
            ?: throw FailedOperationException("Could not find alumni question!")
        val skillAnswer = answers.find { it.key == skillQuestionKey }
            ?: throw FailedOperationException("Could not find skill question!")

        return Student(
            firstnameAnswer.answer.first(),
            lastnameAnswer.answer.first(),
            skillAnswer.answer.map { Skill(it) }.toSortedSet(),
            alumnAnswer.optionId == alumnYesId,
            answers
        )
    }

    /**
     * This function takes a JsonNode representing a question as an input, it will return an Answer object.
     * A question node has a key, label, type, value and depending on the type an options field.
     */
    private fun processQuestionNode(node: JsonNode): Answer {
        val key = node.get("key").asText()
        val label = node.get("label").asText()
        val type = node.get("type").asText()
        assert(key.length == 15 || key.length == 52)

        val valueNode = node.get("value")

        if (valueNode.isNull)
            return Answer(key, label, listOf())

        if (type == "MULTIPLE_CHOICE") {
            val optionId = valueNode.asText()
            val options = node.get("options").toList()
            for(optionNode in options) {
                val id = optionNode.get("id").asText()
                val text = optionNode.get("text").asText()
                if (optionId == id)
                    return Answer(key, label, listOf(text), optionId)
            }
            throw FailedOperationException("The specified option '$optionId' in the answer of the question with '$key' was not found in the associated 'options' field")
        }
        else if (type == "CHECKBOXES" && key.length == 15) {
            val valueIds = valueNode.toSet().map { it.asText() }
            val valueList = mutableListOf<String>()
            val options = node.get("options").toSet()
            for(optionNode in options) {
                val id = optionNode.get("id").asText()
                val text = optionNode.get("text").asText()
                if (valueIds.contains(id))
                    valueList.add(text)
            }
            return Answer(key, label, valueList)
        }
        else if (type == "FILE_UPLOAD") {
            val fileUrls = valueNode.toList().map { it.get("url").asText() }
            return Answer(key, label, fileUrls)
        }

        return Answer(key, label, listOf(valueNode.asText()))
    }
}

@Entity
class Answer(
    val key: String,
    val question: String,
    @ElementCollection
    val answer:  Collection<String>,
    val optionId: String = "") { //TODO: Possibly use optional for optionId
    @Id
    val id: UUID = UUID.randomUUID()
}
