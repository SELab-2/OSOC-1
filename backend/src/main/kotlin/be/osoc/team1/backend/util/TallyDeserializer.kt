package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Answer
import be.osoc.team1.backend.entities.AnswerRegister
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.StudentRegister
import be.osoc.team1.backend.exceptions.FailedOperationException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

/**
 * A deserializer that takes a tally form and extracts the required information to construct a [Student] object.
 */
class TallyDeserializer : StdDeserializer<StudentRegister>(StudentRegister::class.java) {
    object TallyKeys {
        const val firstnameQuestion = "question_nroEGL"
        const val lastnameQuestion = "question_w4KjAo"
        const val alumnQuestion = "question_wz7eGE"
        const val alumnYesId = "689451da-305b-451a-8039-c748ff06ec82"
        const val skillQuestion = "question_3X4q1V"
        const val otherSkillQuestion = "question_w8Ze6o"
        const val studentCoachQuestion = "question_w5Z2eb"
        const val studentCoachYesId = "d2091172-9678-413a-bb3b-0d9cf6d5fa0b"
    }

    override fun deserialize(parser: JsonParser, context: DeserializationContext): StudentRegister {
        val rootNode: JsonNode = parser.codec.readTree(parser)
        val fields = rootNode.get("data").get("fields").toList()
        val answerMap = mutableMapOf<String, AnswerRegister>()
        for (field in fields) {
            val answer = processQuestionNode(field)
            answerMap[answer.key] = answer
        }

        try {
            val skillNames = getAnswerForKey(answerMap, TallyKeys.skillQuestion, "skill").answer.toMutableSet()
            if (skillNames.remove("Other")) {
                skillNames.add(
                    getAnswerForKey(answerMap, TallyKeys.otherSkillQuestion, "otherSkill").answer.first()
                )
            }

            return StudentRegister(
                getAnswerForKey(answerMap, TallyKeys.firstnameQuestion, "firstname").answer.first(),
                getAnswerForKey(answerMap, TallyKeys.lastnameQuestion, "lastname").answer.first(),
                "",
                skillNames.map { Skill(it) }.toSortedSet(),
                getAnswerForKey(answerMap, TallyKeys.alumnQuestion, "alumni").optionId == TallyKeys.alumnYesId,
                getAnswerForKey(
                    answerMap, TallyKeys.studentCoachQuestion, "studentCoach"
                ).optionId == TallyKeys.studentCoachYesId,
                answerMap.values.toList(),
            )
        } catch (nse: NoSuchElementException) {
            throw FailedOperationException("The firstname, lastname or other skill answer was found to be empty!")
        }
    }

    private fun getAnswerForKey(answerMap: Map<String, AnswerRegister>, key: String, questionName: String): AnswerRegister =
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
    private fun processQuestionNode(node: JsonNode): AnswerRegister {
        val key = node.get("key").asText()
        val label = node.get("label").asText()
        val type = node.get("type").asText()
        val valueNode = node.get("value")

        if (valueNode.isNull)
            return AnswerRegister(key, label, listOf())

        return when (type) {
            "MULTIPLE_CHOICE" -> getAnswerMultipleChoice(node, valueNode, key, label)
            "CHECKBOXES" -> AnswerRegister(key, label, getAnswerListCheckboxes(node, valueNode, key))
            "FILE_UPLOAD" -> AnswerRegister(key, label, getAnswerListFileUpload(valueNode))
            else -> AnswerRegister(key, label, listOf(valueNode.asText()))
        }
    }

    /**
     * Extract the answer and optionId out of a json object called [node] which represents a question.
     * This function assumes the question is of type MULTIPLE_CHOICE.
     */
    private fun getAnswerMultipleChoice(node: JsonNode, valueNode: JsonNode, key: String, label: String): AnswerRegister {
        val optionId = valueNode.asText()
        val options = node.get("options").toSet()
        val optionNode = options.find { it.get("id").asText() == optionId }
            ?: throw FailedOperationException("The specified option '$optionId' in the answer of the question with '$key' was not found in the associated 'options' field")
        return AnswerRegister(key, label, listOf(optionNode.get("text").asText()), optionId)
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
