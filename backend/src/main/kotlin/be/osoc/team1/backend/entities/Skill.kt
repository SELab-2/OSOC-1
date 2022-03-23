package be.osoc.team1.backend.entities

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Skill(
    @Id
    val skillName: String

)
