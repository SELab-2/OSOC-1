package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Student
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface AssignmentRepository : CrudRepository<Assignment, UUID> {
    fun findByStudent(student: Student): Set<Assignment>
}
