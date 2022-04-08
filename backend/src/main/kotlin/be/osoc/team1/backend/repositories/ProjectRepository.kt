package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Project
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ProjectRepository : CrudRepository<Project, UUID> {
    fun findByOrganizationAndEditionName(organization: String, editionName: String): Collection<Project>
}
