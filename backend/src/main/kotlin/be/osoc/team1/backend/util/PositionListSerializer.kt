package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Position

/**
 * This class is used to serialize a list of [Position] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
class PositionListSerializer : ListSerializer<Position>({ "/positions/" + it.id.toString() })
