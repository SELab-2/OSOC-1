package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Student
import org.springframework.data.repository.CrudRepository

interface StudentRepository : CrudRepository<Student, Long>