package be.osoc.team1.backend.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Student (
    @Id
    @GeneratedValue
    val id: Long,

    val firstName: String,

    val lastName: String
)