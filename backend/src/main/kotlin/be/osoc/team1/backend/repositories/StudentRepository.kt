package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Student
import org.springframework.data.repository.CrudRepository
import java.util.*

interface StudentRepository : CrudRepository<Student, UUID>
