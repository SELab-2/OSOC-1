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

        val firstnameAnswer = answerMap[nameQuestionKey]
            ?: throw FailedOperationException("Could not find firstname question!")
        val lastnameAnswer = answerMap[lastnameQuestionKey]
            ?: throw FailedOperationException("Could not find lastname question!")
        val alumnAnswer = answerMap[alumnQuestionKey]
            ?: throw FailedOperationException("Could not find alumni question!")
        val skillAnswer = answerMap[skillQuestionKey]
            ?: throw FailedOperationException("Could not find skill question!")

        try {
            return Student(
                firstnameAnswer.answer.first(),
                lastnameAnswer.answer.first(),
                skillAnswer.answer.map { Skill(it) }.toSortedSet(),
                alumnAnswer.optionId == alumnYesId,
                answerMap.values.toList()
            )
        } catch (_: NoSuchElementException) {
            throw FailedOperationException("The firstname ore lastname answer was found to be empty!")
        }
    }

    /**
     * This function takes a [JsonNode] representing a question as an input, it will return an [Answer] object.
     * A question node has a key, label, type, value and depending on the type, an options field.
     */
    private fun processQuestionNode(node: JsonNode): Answer {
        val key = node.get("key").asText()
        val label = node.get("label").asText()
        val type = node.get("type").asText()

        val valueNode = node.get("value")

        if (valueNode.isNull)
            return Answer(key, label, listOf())

        if (type == "MULTIPLE_CHOICE") {
            val optionId = valueNode.asText()
            val options = node.get("options").toList()
            for (optionNode in options) {
                val id = optionNode.get("id").asText()
                val text = optionNode.get("text").asText()
                if (optionId == id)
                    return Answer(key, label, listOf(text), optionId)
            }
            throw FailedOperationException("The specified option '$optionId' in the answer of the question with '$key' was not found in the associated 'options' field")
        } else if (type == "CHECKBOXES" && key.length == 15) {
            val valueIds = valueNode.toSet().map { it.asText() }
            val valueList = mutableListOf<String>()
            val options = node.get("options").toSet()
            for (optionNode in options) {
                val id = optionNode.get("id").asText()
                val text = optionNode.get("text").asText()
                if (valueIds.contains(id))
                    valueList.add(text)
            }
            return Answer(key, label, valueList)
        } else if (type == "FILE_UPLOAD") {
            val fileUrls = valueNode.toList().map { it.get("url").asText() }
            return Answer(key, label, fileUrls)
        }

        return Answer(key, label, listOf(valueNode.asText()))
    }
}
