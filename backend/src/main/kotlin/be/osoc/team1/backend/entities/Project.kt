package be.osoc.team1.backend.entities

import be.osoc.team1.backend.util.StudentListSerializer
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.OneToMany

/**
 * Represents a project in the database. A project is constructed with a [name]
 * and a [description]. Note that neither of these fields, nor the combination of both of them need be unique.
 * A project also has [students], which is a list of the students assigned to this project
 * Finally, a project also has [coaches], which is a list of coaches who will be aiding with this project
 */
@Entity
class Project(
    val name: String,

    val description: String,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JsonSerialize(using = StudentListSerializer::class)
    val students: MutableCollection<Student> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL])
    @JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator::class)
    val coaches: MutableCollection<User> = mutableListOf()
) {
    @Id
    val id: UUID = UUID.randomUUID()
}
