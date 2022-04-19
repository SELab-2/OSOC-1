package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.StatusSuggestion
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface StatusSuggestionRepository : CrudRepository<StatusSuggestion, UUID>
