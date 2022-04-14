package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.ActiveEdition
import org.springframework.data.repository.CrudRepository

interface EditionRepository : CrudRepository<ActiveEdition, String>