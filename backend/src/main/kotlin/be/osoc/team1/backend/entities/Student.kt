package be.osoc.team1.backend.entities

import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Student(val firstName: String, val lastName: String) {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID()
}
