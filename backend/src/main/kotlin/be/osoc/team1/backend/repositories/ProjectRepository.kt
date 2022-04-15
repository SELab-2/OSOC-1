package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Project
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ProjectRepository : CrudRepository<Project, UUID> {
    fun findByEdition(edition: String): Collection<Project>
    fun existsByIdAndEdition(id: UUID, edition: String): Boolean
    fun findByIdAndEdition(id: UUID, edition: String): Project?
}
