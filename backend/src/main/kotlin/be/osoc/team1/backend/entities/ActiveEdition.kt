package be.osoc.team1.backend.entities

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Represents an [name] that is currently active. As we're currently restricting the amount of active editions
 * at once to one, there should only be at most one record of this entity in the database at all times.
 */
@Entity
class ActiveEdition(@Id val name: String)
