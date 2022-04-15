package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.Project
import be.osoc.team1.backend.exceptions.InvalidCommunicationIdException
import be.osoc.team1.backend.exceptions.InvalidProjectIdException
import be.osoc.team1.backend.repositories.CommunicationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommunicationService(private val repository: CommunicationRepository) {

    /**
     * Get a communication by its [id], if this id doesn't exist throw an InvalidCommunicationIdException
     */
    fun getCommunicationById(id: UUID): Communication = repository.findByIdOrNull(id) ?: throw InvalidCommunicationIdException()

    /**
     * Creates a new communication based on [communication]. Returns the created communication object.
     */
    fun createCommunication(communication: Communication): Communication = repository.save(communication)
}
