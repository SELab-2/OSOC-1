package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.services.StudentService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class StudentController(private val service: StudentService) {

    @GetMapping("/students")
    fun getAllStudents() = service.getAllStudents()

    @GetMapping("/students/{id}")
    fun getStudentById(@PathVariable id: Long) = service.getStudentById(id)

    @DeleteMapping("/students/{id}")
    fun deleteStudentById(@PathVariable id: Long) = service.deleteStudentById(id)

    @PutMapping("/students/create")
    fun putStudent(@RequestBody student: Student) = service.putStudent(student)
}
