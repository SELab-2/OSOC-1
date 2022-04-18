package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Answer
import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.AnswerRepository
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.repositories.PositionRepository
import be.osoc.team1.backend.repositories.StatusSuggestionRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

open class BaseService<T>(open val repository: CrudRepository<T, UUID>) {

    /**
     * Get a assignment by its [id], if this id doesn't exist throw an [InvalidIdException]
     */
    fun getById(id: UUID): T = repository.findByIdOrNull(id)
        ?: throw InvalidIdException()
}

@Service
class AssignmentService(repository: AssignmentRepository) : BaseService<Assignment>(repository)

@Service
class PositionService(repository: PositionRepository) : BaseService<Position>(repository)

@Service
class StatusSuggestionService(repository: StatusSuggestionRepository) : BaseService<StatusSuggestion>(repository)

@Service
class AnswerService(repository: AnswerRepository) : BaseService<Answer>(repository)
