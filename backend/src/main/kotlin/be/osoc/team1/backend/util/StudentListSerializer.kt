package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Student
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * This class is used to serialize a list of [Student] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
class StudentListSerializer protected constructor(t: Class<List<Student>>?) : StdSerializer<List<Student>>(t) {
    constructor() : this(null) {}

    override fun serialize(students: List<Student>?, gen: JsonGenerator?, provider: SerializerProvider?) {
        val baseUrl: String = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        if (gen != null) {
            gen.writeStartArray()
            if (students != null) {
                for (s in students) {
                    gen.writeObject(baseUrl + "/students/" + s.id.toString())
                }
            }
            gen.writeEndArray()
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
