package be.osoc.team1.backend.entities

import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * Represents a coach in the database. A coach is constructed with a [firstName]
 * and a [lastName]. Note that neither of these fields, nor the combination of both of them need be unique.
 * I.e. there could be two coaches in the database with [firstName] "Tom" and [lastName] "Alard".
 */
@Entity
class Coach(
    val firstName: String,
    val lastName: String
) {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID()
}
