package be.osoc.team1.backend.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * This class is used to serialize a list of [T] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
open class Serializer<T>(private val genFunc: (T) -> String, t: Class<T>?) : StdSerializer<T>(t) {
    constructor(func: (T) -> String) : this(func, null) {}

    override fun serialize(item: T?, gen: JsonGenerator?, provider: SerializerProvider?) {
        val baseUrl: String = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        gen!!.writeObject(baseUrl + genFunc(item!!))
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
