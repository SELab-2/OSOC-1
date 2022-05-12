package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Answer
import be.osoc.team1.backend.entities.Assignment
import be.osoc.team1.backend.entities.Position
import be.osoc.team1.backend.entities.Skill
import be.osoc.team1.backend.entities.StatusSuggestion
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.repositories.AnswerRepository
import be.osoc.team1.backend.repositories.AssignmentRepository
import be.osoc.team1.backend.repositories.PositionRepository
import be.osoc.team1.backend.repositories.SkillRepository
import be.osoc.team1.backend.repositories.StatusSuggestionRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

open class BaseService<T, K>(open val repository: CrudRepository<T, K>) {

    /**
     * Get a [T] by its [id], if this [id] doesn't exist throw an [InvalidIdException]
     */
    fun getById(id: K): T = repository.findByIdOrNull(id) ?: throw InvalidIdException()

    /**
     * Get all objects from [repository].
     */
    fun getAll(): Iterable<T> = repository.findAll()
}

@Service
class AssignmentService(repository: AssignmentRepository) : BaseService<Assignment, UUID>(repository)

@Service
class PositionService(repository: PositionRepository) : BaseService<Position, UUID>(repository)

@Service
class StatusSuggestionService(repository: StatusSuggestionRepository) : BaseService<StatusSuggestion, UUID>(repository)

@Service
class AnswerService(override val repository: AnswerRepository) : BaseService<Answer, UUID>(repository) {

    fun getAnswersByStudent(student: Student): Iterable<Answer> {
        //val answers = repository.findAll()
        //val resultingAnswers = mutableListOf<Answer>()
        //for (answer: Answer in answers){
        //    if (answer.studentId == student.id){
        //        resultingAnswers.add(answer)
        //    }
        //}
        //return resultingAnswers
        return repository.findByStudentId(student.id)
    }
}

@Service
class SkillService(repository: SkillRepository) : BaseService<Skill, String>(repository)
