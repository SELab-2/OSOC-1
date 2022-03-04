package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.StudentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class StudentController(private val service: StudentService) {

    @GetMapping("/students")
    fun getAllStudents() = service.getAllStudents()

    @GetMapping("/students/{id}")
    fun getStudentById(@PathVariable id: UUID): Student {
        try {
            return service.getStudentById(id)
        } catch (_: InvalidIdException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No student found with given id")
        }
    }

    @DeleteMapping("/students/{id}")
    fun deleteStudentById(@PathVariable id: UUID) {
        try {
            service.deleteStudentById(id)
        } catch (_: InvalidIdException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No student found with given id")
        }
    }

    @PutMapping("/students/create")
    fun putStudent(@RequestBody student: Student) {
        try {
            service.putStudent(student)
        } catch (_: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Student with given id already exists")
        }
    }
}
