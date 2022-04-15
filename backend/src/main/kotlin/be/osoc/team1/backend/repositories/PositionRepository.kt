package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Position
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface PositionRepository : CrudRepository<Position, UUID>
