package be.osoc.team1.backend.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * This class is used to serialize a list of [T] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
open class ListSerializer<T>(private val genFunc: (T) -> String, t: Class<List<T>>?) : StdSerializer<List<T>>(t) {
    constructor(func: (T) -> String) : this(func, null) {}

    override fun serialize(items: List<T>?, gen: JsonGenerator?, provider: SerializerProvider?) {
        val baseUrl: String = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        if (gen != null) {
            gen.writeStartArray()
            if (items != null) {
                for (s in items) {
                    gen.writeObject(baseUrl + genFunc(s))
                }
            }
            gen.writeEndArray()
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
