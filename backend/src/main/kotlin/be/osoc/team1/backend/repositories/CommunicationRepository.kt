package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Communication
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface CommunicationRepository : CrudRepository<Communication, UUID> {
    fun findByIdAndEdition(id: UUID, edition: String): Communication?
}
