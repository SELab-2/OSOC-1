package be.osoc.team1.backend.entities

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Represents a skill that a student has and that could be required to get certain roles on a project.
 */
@Entity
class Skill(
    @Id
    val skillName: String
)
