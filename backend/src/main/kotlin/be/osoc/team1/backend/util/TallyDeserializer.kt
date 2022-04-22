package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Answer
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.FailedOperationException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

/**
 * A deserializer that takes a tally form and extracts the required information to construct a [Student] object.
 */
class TallyDeserializer : StdDeserializer<Student>(Student::class.java) {
    object TallyKeys {
        const val firstnameQuestion = "question_nroEGL"
        const val lastnameQuestion = "question_w4KjAo"
        const val alumnQuestion = "question_wz7eGE"
        const val alumnYesId = "689451da-305b-451a-8039-c748ff06ec82"
        const val skillQuestion = "question_3X4q1V"
    }

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Student {
        val rootNode: JsonNode = parser.codec.readTree(parser)
        val fields = rootNode.get("data").get("fields").toList()
        val answerMap = mutableMapOf<String, Answer>()
        for (field in fields) {
            val answer = processQuestionNode(field)
            answerMap[answer.key] = answer
        }

        try {
            return Student(
                getAnswerForKey(answerMap, TallyKeys.firstnameQuestion, "firstname").answer.first(),
                getAnswerForKey(answerMap, TallyKeys.lastnameQuestion, "lastname").answer.first(),
                "",
                getAnswerForKey(answerMap, TallyKeys.skillQuestion, "skill").answer.map { Skill(it) }.toSortedSet(),
                getAnswerForKey(answerMap, TallyKeys.alumnQuestion, "alumni").optionId == TallyKeys.alumnYesId,
                answerMap.values.toList()
            )
        } catch (_: NoSuchElementException) {
            throw FailedOperationException("The firstname ore lastname answer was found to be empty!")
        }
    }

    private fun getAnswerForKey(answerMap: Map<String, Answer>, key: String, questionName: String): Answer =
        answerMap[key] ?: throw FailedOperationException("Could not find $questionName question!")

    /**
     * This function takes in a [JsonNode] representing a question, and it will return an [Answer] object.
     * This [node] has a key, label, type, value and depending on the type, an options field.
     * The [node] object should look like this:
     * ```
     * {
     *     "key": QUESTION_KEY,
     *     "label": QUESTION_LABEL,
     *     "type": QUESTION_TYPE,
     *     "value": VALUE_OBJECT
     * }
     * ```
     */
    private fun processQuestionNode(node: JsonNode): Answer {
        val key = node.get("key").asText()
        val label = node.get("label").asText()
        val type = node.get("type").asText()
        val valueNode = node.get("value")

        if (valueNode.isNull)
            return Answer(key, label, listOf())

        return when (type) {
            "MULTIPLE_CHOICE" -> getAnswerMultipleChoice(node, valueNode, key, label)
            "CHECKBOXES" -> Answer(key, label, getAnswerListCheckboxes(node, valueNode, key))
            "FILE_UPLOAD" -> Answer(key, label, getAnswerListFileUpload(valueNode))
            else -> Answer(key, label, listOf(valueNode.asText()))
        }
    }

    /**
     * Extract the answer and optionId out of a json object called [node] which represents a question.
     * This function assumes the question is of type MULTIPLE_CHOICE.
     */
    private fun getAnswerMultipleChoice(node: JsonNode, valueNode: JsonNode, key: String, label: String): Answer {
        val optionId = valueNode.asText()
        val options = node.get("options").toSet()
        val optionNode = options.find { it.get("id").asText() == optionId }
            ?: throw FailedOperationException("The specified option '$optionId' in the answer of the question with '$key' was not found in the associated 'options' field")
        return Answer(key, label, listOf(optionNode.get("text").asText()), optionId)
    }

    /**
     * Extract the answers out of a json object called [node] which represents a question.
     * This function assumes the question is of type CHECKBOXES.
     */
    private fun getAnswerListCheckboxes(node: JsonNode, valueNode: JsonNode, key: String): List<String> {
        if (key.length != 15)
            return listOf(valueNode.asText())

        val valueList = mutableListOf<String>()
        val valueIds = valueNode.toSet().map { it.asText() }
        for (optionNode in node.get("options").toSet()) {
            val id = optionNode.get("id").asText()
            if (valueIds.contains(id))
                valueList.add(optionNode.get("text").asText())
        }
        return valueList
    }

    /**
     * Extract the urls out of a [valueNode] json array containing file upload objects.
     * These file upload objects are structured like this:
     * ```
     * {
     *     "id": ID,
     *     "name": FILENAME,
     *     "url": URL,
     *     "mimeType": MIMETYPE,
     *     "size": SIZE_IN_BYTES
     * }
     * ```
     * This function assumes that [valueNode] is "value" property of a question of type FILE_UPLOAD.
     */
    private fun getAnswerListFileUpload(valueNode: JsonNode): List<String> =
        valueNode.toList().map { it.get("url").asText() }
}
