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
    private val nameQuestionKey = "question_nroEGL"
    private val lastnameQuestionKey = "question_w4KjAo"
    private val alumnQuestionKey = "question_wz7eGE"
    private val alumnYesId = "689451da-305b-451a-8039-c748ff06ec82"
    private val skillQuestionKey = "question_3X4q1V"

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
                getAnswerForKey(answerMap, nameQuestionKey, "firstname").answer.first(),
                getAnswerForKey(answerMap, lastnameQuestionKey, "lastname").answer.first(),
                getAnswerForKey(answerMap, skillQuestionKey, "skill").answer.map { Skill(it) }.toSortedSet(),
                getAnswerForKey(answerMap, alumnQuestionKey, "alumni").optionId == alumnYesId,
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
     */
    private fun processQuestionNode(node: JsonNode): Answer {
        val key = node.get("key").asText()
        val label = node.get("label").asText()
        val type = node.get("type").asText()

        val valueNode = node.get("value")

        if (valueNode.isNull)
            return Answer(key, label, listOf())

        return when(type) {
            "MULTIPLE_CHOICE" -> getAnswerMultipleChoice(node, valueNode, key, label)
            "CHECKBOXES" -> Answer(key, label, getAnswerListCheckboxes(node, valueNode, key))
            "FILE_UPLOAD" -> Answer(key, label, getAnswerListFileUpload(valueNode))
            else -> Answer(key, label, listOf(valueNode.asText()))
        }
    }

    private fun getAnswerMultipleChoice(node: JsonNode, valueNode: JsonNode, key: String, label: String): Answer {
        val optionId = valueNode.asText()
        val options = node.get("options").toSet()
        val optionNode = options.find { it.get("id").asText() == optionId } ?:
            throw FailedOperationException("The specified option '$optionId' in the answer of the question with '$key' was not found in the associated 'options' field")
        return Answer(key, label, listOf(optionNode.get("text").asText()), optionId)
    }

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

    private fun getAnswerListFileUpload(valueNode: JsonNode): List<String> {
        return valueNode.toList().map { it.get("url").asText() }
    }
}
