package be.osoc.team1.backend.entities

import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne

enum class TypeEnum {
    Email
}

/**
 * Represents communication in the database. Communication is constructed with a [student], [type] and a [message]
 * Note that neither of these fields, nor the combination of all of them need to be unique.
 */
@Entity
class Communication(
    val message: String,

    val type: TypeEnum,

    @OneToOne(cascade = [CascadeType.ALL])
    val student: Student
) {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID()
}
