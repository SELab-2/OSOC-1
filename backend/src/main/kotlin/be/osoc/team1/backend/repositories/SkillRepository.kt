package be.osoc.team1.backend.repositories

import be.osoc.team1.backend.entities.Skill
import org.springframework.data.repository.CrudRepository

interface SkillRepository : CrudRepository<Skill, String>
