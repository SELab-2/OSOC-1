package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Answer
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface AnswerRepository : CrudRepository<Answer, UUID>
