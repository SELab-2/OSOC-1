package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.exceptions.InvalidStatusSuggestionIdException
import be.osoc.team1.backend.repositories.StatusSuggestionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class StatusSuggestionService(private val repository: StatusSuggestionRepository) {

    /**
     * Get a statusSuggestion by its [id], if this id doesn't exist throw an [InvalidStatusSuggestionIdException]
     */
    fun getStatusSuggestionById(id: UUID): StatusSuggestion = repository.findByIdOrNull(id)
            ?: throw InvalidStatusSuggestionIdException()
}
