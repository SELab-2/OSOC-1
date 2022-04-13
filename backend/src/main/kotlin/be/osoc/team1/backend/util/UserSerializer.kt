package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.User

/**
 * This class is used to serialize a list of [User] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
class UserSerializer : Serializer<User>({ "/users/" + it.id.toString() })
