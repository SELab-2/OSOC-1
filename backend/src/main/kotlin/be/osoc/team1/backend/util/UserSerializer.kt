package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.User

/**
 * This class is used to serialize a [User] object when it is used as a reference.
 * This makes sure it gets turned into the rest API url.
 */
class UserSerializer : Serializer<User>({ "/users/" + it.id.toString() })
