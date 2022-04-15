package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.exceptions.InvalidPositionIdException
import be.osoc.team1.backend.repositories.PositionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PositionService(private val repository: PositionRepository) {

    /**
     * Get a position by its [id], if this id doesn't exist throw an [InvalidPositionIdException]
     */
    fun getPositionById(id: UUID): Position = repository.findByIdOrNull(id)
        ?: throw InvalidPositionIdException()
}
