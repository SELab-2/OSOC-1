package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Student
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.UUID

interface StudentRepository : PagingAndSortingRepository<Student, UUID> {
    fun findByEdition(edition: String, sortBy: Sort): Iterable<Student>
}
