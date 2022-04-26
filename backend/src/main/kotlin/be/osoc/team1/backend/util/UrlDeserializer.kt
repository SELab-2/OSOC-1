package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.repositories.AssignmentRepository
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

/**
 * This class deserializes a url into an object of type [T]. It does by extracting the uuid from
 * the url and then looking up this id in the [repository] for type [T].
 */
open class UrlDeserializer<T>(private val repository: CrudRepository<T, UUID>) :
    JsonDeserializer<T>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): T {
        val rootNode: JsonNode = parser.codec.readTree(parser)
        val url = rootNode.asText()
        val id = UUID.fromString(url.substring(url.indexOfLast { it == '/' } + 1))

        return repository.findByIdOrNull(id) ?: throw FailedOperationException("Could not find $id")
    }
}

class AssignmentDeserializer(assignmentRepository: AssignmentRepository) :
    UrlDeserializer<Assignment>(assignmentRepository)
