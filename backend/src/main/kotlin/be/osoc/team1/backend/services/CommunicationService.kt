package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.GenericEditionRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CommunicationService(private val repository: GenericEditionRepository<Communication>) {

    fun getCommunicationById(id: UUID, edition: String) = repository.findByIdAndEdition(id, edition)
        ?: throw InvalidIdException()

    /**
     * Creates a new communication based on [communication]. Returns the created communication object.
     */
    fun createCommunication(communication: Communication): Communication = repository.save(communication)
}
