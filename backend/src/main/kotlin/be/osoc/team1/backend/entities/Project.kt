package be.osoc.team1.backend.entities

import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class Project(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID,

    val name: String,

    val desc: String,

    @OneToMany(cascade = [CascadeType.ALL])
    val students: MutableCollection<Student>
)