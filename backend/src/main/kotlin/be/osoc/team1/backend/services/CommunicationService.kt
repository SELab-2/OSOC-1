package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.repositories.CommunicationRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CommunicationService(private val repository: CommunicationRepository) {

    /**
     * Creates a new communication based on [communication]
     */
    fun createCommunication(communication: Communication): UUID = repository.save(communication).id
}
