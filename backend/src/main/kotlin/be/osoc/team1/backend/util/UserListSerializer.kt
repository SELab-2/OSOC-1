package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.User
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class UserListSerializer protected constructor(t: Class<List<User>>?) : StdSerializer<List<User>>(t) {
    constructor() : this(null) {}

    override fun serialize(users: List<User>?, gen: JsonGenerator?, provider: SerializerProvider?) {
        if (gen != null) {
            gen.writeStartArray()
            if (users != null) {
                for (u in users) {
                    gen.writeObject(BaseUrlUtil.getBaseUrl() + "/users/" + u.id.toString())
                }
            }
            gen.writeEndArray()
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
