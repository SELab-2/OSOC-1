package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Project
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ProjectRepository : CrudRepository<Project, UUID>
