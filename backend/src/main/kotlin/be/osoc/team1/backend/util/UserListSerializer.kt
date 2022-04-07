package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.User
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * This class is used to serialize a list of [User] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
class UserListSerializer protected constructor(t: Class<List<User>>?) : StdSerializer<List<User>>(t) {
    constructor() : this(null) {}

    override fun serialize(users: List<User>?, gen: JsonGenerator?, provider: SerializerProvider?) {
        val baseUrl: String = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        if (gen != null) {
            gen.writeStartArray()
            if (users != null) {
                for (u in users) {
                    gen.writeObject(baseUrl + "/users/" + u.id.toString())
                }
            }
            gen.writeEndArray()
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
