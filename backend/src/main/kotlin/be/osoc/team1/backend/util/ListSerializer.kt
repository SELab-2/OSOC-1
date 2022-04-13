package be.osoc.team1.backend.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * This class is used to serialize a list of [T] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
open class ListSerializer<T>(private val genFunc: (T) -> String) : BaseSerializer<List<T>>() {

    override fun serialize(items: List<T>?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen!!.writeStartArray()
        for (s in items!!) {
            gen.writeObject(baseUrl + genFunc(s))
        }
        gen.writeEndArray()
    }
}
