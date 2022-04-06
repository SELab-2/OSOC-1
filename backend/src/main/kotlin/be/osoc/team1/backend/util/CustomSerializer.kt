package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.util.BaseUrlUtil.getBaseUrl
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class StudentListSerializer protected constructor(t: Class<List<Student>>?) : StdSerializer<List<Student>>(t) {
    constructor() : this(null) {}

    override fun serialize(students: List<Student>?, gen: JsonGenerator?, provider: SerializerProvider?) {
        if (gen != null) {
            gen.writeStartArray()
            if (students != null) {
                for (p in students) {
                    gen.writeObject(getBaseUrl() + "/students/" + p.id.toString())
                }
            }
            gen.writeEndArray()
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}