package be.osoc.team1.backend.entities

import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

enum class CommunicationTypeEnum {
    Email
}

/**
 * Represents communication in the database. Communication is constructed with a [type] and a [message]
 * Note that neither of these fields, nor the combination of both of them need to be unique.
 */
@Entity
class Communication(
    val message: String,
    val type: CommunicationTypeEnum
) {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: UUID = UUID.randomUUID()
}
