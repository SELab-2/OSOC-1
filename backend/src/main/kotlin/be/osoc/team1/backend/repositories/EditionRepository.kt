package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Edition
import org.springframework.data.repository.CrudRepository

interface EditionRepository : CrudRepository<Edition, String>