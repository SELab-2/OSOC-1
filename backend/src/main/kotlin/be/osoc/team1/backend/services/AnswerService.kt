package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Answer
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.repositories.AnswerRepository
import org.springframework.stereotype.Service

@Service
class AnswerService(val repository: AnswerRepository) {

    fun getAnswersByStudent(student: Student): Iterable<Answer> {
        return repository.findByStudentId(student.id)
    }
}
