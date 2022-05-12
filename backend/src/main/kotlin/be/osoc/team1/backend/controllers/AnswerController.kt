package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Answer
import be.osoc.team1.backend.services.AnswerService
import be.osoc.team1.backend.services.StudentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("{edition}/answers")
class AnswerController(val service: AnswerService, val studentService: StudentService) {

    @GetMapping("/{studentId}")
    fun getAnswers(
        @PathVariable studentId: UUID,
        @PathVariable edition: String,
    ): Iterable<Answer> {
        val student = studentService.getStudentById(studentId, edition)
        return service.getAnswersByStudent(student)
    }
}
