package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.exceptions.InvalidAssignmentIdException
import be.osoc.team1.backend.exceptions.InvalidStatusSuggestionIdException
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.repositories.StatusSuggestionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class AssignmentService(private val repository: AssignmentRepository) {

    /**
     * Get a assignment by its [id], if this id doesn't exist throw an [InvalidAssignmentIdException]
     */
    fun getAssignmentById(id: UUID): Assignment = repository.findByIdOrNull(id)
            ?: throw InvalidAssignmentIdException()
}
