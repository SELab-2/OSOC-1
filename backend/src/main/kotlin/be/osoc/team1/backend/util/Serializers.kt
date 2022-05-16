package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * This class is used to serialize a [T] object when it is used as a reference.
 * This makes sure it gets turned into the rest API url.
 */
open class Serializer<T>(private val genFunc: (T) -> String) : BaseSerializer<T>() {

    fun toUrl(item: T): String = baseUrl + genFunc(item)

    override fun serialize(item: T?, gen: JsonGenerator?, provider: SerializerProvider?) =
        gen!!.writeObject(toUrl(item!!))
}

class PositionSerializer : Serializer<Position>({ "/positions/${it.id}" })

class StudentSerializer : Serializer<Student>({ "/${it.edition}/students/${it.id}" })

class ProjectSerializer : Serializer<Project>({ "/${it.edition}/projects/${it.id}" })

class UserSerializer : Serializer<User>({ "/users/${it.id}" })
