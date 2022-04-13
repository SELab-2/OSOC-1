package be.osoc.team1.backend.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * This class is used to serialize a [T] object when it is used as a reference.
 * This makes sure it gets turned into the rest API url.
 */
open class Serializer<T>(private val genFunc: (T) -> String) : BaseSerializer<T>() {

    override fun serialize(item: T?, gen: JsonGenerator?, provider: SerializerProvider?) =
        gen!!.writeObject(baseUrl + genFunc(item!!))
}
