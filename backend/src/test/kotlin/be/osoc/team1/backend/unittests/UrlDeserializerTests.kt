package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.util.AssignmentDeserializer
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.io.InputStream
import java.util.UUID

class UrlDeserializerTests {

    @Test
    fun `FailedOperationException is thrown when an invalid id is deserialized`() {
        val id = UUID.randomUUID()

        val repository: AssignmentRepository = mockk()
        every { repository.findByIdOrNull(id) } returns null
        val assignmentDeserializer = AssignmentDeserializer(repository)

        val json = "\"https://localhost:3000/assignments/$id\""

        val mapper = ObjectMapper()
        val stream: InputStream = json.byteInputStream()
        val parser: JsonParser = mapper.factory.createParser(stream)
        val context: DeserializationContext = mapper.deserializationContext
        assertThrows<FailedOperationException> { assignmentDeserializer.deserialize(parser, context) }
    }
}