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

open class ListDeserializer<T>(private val repository: CrudRepository<T, UUID>) :
    JsonDeserializer<List<T>>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): List<T> {
        val rootNode: JsonNode = parser.codec.readTree(parser)
        val ids = rootNode.toList().map { it.asText() }
            .map { url -> UUID.fromString(url.substring(url.indexOfLast { it == '/' } + 1)) }

        val result = mutableListOf<T>()
        for (id in ids) result.add(
            repository.findByIdOrNull(id) ?: throw FailedOperationException("Could not find $id")
        )
        return result
    }
}

class AssignmentListDeserializer(assignmentRepository: AssignmentRepository) :
    ListDeserializer<Assignment>(assignmentRepository)
