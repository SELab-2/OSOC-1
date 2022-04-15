package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.Position

/**
 * This class is used to serialize a [Position] object when it is used as a reference.
 * This makes sure it gets turned into the rest API url.
 */
class PositionSerializer : Serializer<Position>({ "/positions/" + it.id.toString() })
