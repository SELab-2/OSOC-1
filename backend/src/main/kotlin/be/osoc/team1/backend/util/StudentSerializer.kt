package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Student

/**
 * This class is used to serialize a list of [Student] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
class StudentSerializer : Serializer<Student>({ "/students/" + it.id.toString() })
