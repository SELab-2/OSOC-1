package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.repositories.CommunicationRepository
import org.springframework.stereotype.Service

@Service
class CommunicationService(repository: CommunicationRepository) : BaseService<Communication>(repository) {

    /**
     * Creates a new communication based on [communication]. Returns the created communication object.
     */
    fun createCommunication(communication: Communication): Communication = repository.save(communication)
}
