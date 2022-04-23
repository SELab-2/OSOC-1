package be.osoc.team1.backend.repositories

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface GenericEditionRepository<T> : CrudRepository<T, UUID> {
    fun findByIdAndEdition(id: UUID, edition: String): T?
}
