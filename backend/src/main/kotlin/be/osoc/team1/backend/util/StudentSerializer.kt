package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Student

/**
 * This class is used to serialize a [Student] object when it is used as a reference.
 * This makes sure it gets turned into the rest API url.
 */
class StudentSerializer : Serializer<Student>({ "/students/" + it.id.toString() })
