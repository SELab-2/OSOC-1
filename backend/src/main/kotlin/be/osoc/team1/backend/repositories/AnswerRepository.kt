package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Answer
import be.osoc.team1.backend.entities.Student
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface AnswerRepository : CrudRepository<Answer, UUID>{
    fun findByStudentId(studentId: UUID): Iterable<Answer>
}
