package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Assignment

/**
 * This class is used to serialize a list of [Assignment] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
class AssignmentListSerializer : ListSerializer<Assignment>({ "/assignments/" + it.id.toString() })
