package be.osoc.team1.backend.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.Date
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.validation.constraints.NotBlank

enum class CommunicationTypeEnum {
    Email
}

/**
 * Represents communication in the database. Communication is constructed with a [type] and a [message]
 * Note that neither of these fields, nor the combination of both of them need to be unique. When creating a
 * communication, it will also get a [registrationTime], you can pass this as a parameter or use the default value which
 * will just set the [registrationTime] to the current time.
 */
@Entity
class Communication(
    val message: String,
    val type: CommunicationTypeEnum,
    @JsonIgnore
    @NotBlank
    val edition: String = "",
    @JsonIgnore
    @OneToOne
    val student: Student,
    val registrationTime: Date = Date()
) {
    @Id
    var id: UUID = UUID.randomUUID()
}

data class CommunicationDTO(val message: String, val type: CommunicationTypeEnum)
