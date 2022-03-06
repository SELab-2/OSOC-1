package be.osoc.team1.backend.entities

import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

enum class StatusEnum {
    Yes, Maybe, No, Undecided
}

enum class SuggestionEnum {
    Yes, Maybe, No
}

// https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/
@Entity
class StatusSuggestion(
    @ManyToOne(fetch = FetchType.LAZY)
    val student: Student,
    val status: SuggestionEnum,
    val motivation: String) {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID()
}

@Entity
class Student(
    val firstName: String,
    val lastName: String,
    var status: StatusEnum = StatusEnum.Undecided,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusSuggestions: MutableList<StatusSuggestion> = mutableListOf()) {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID()
}
