package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Student
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.UUID

interface StudentRepository : PagingAndSortingRepository<Student, UUID> {
    fun findByOrganizationAndEditionName(organization: String, editionName: String, pageable: Pageable): Page<Student>
}
