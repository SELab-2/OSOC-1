package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.services.CommunicationService
import be.osoc.team1.backend.services.StudentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/communications")
class CommunicationController(
    private val communicationService: CommunicationService,
    private val studentService: StudentService
) {

    /**
     * Gets all communications that belongs to a student, if this [studentId] doesn't exist the service will return a 404
     */
    @GetMapping("/{studentId}")
    fun getCommunicationsByStudentId(@PathVariable studentId: UUID): Collection<Communication> =
        studentService.getStudentById(studentId).communications

    /**
     * Creates a communication from the request body, this can also override an already existing communication
     * returns the id of the communication
     */
    @PostMapping("/{studentId}")
    fun postCommunication(@PathVariable studentId: UUID, @RequestBody communication: Communication): UUID {
        val id: UUID = communicationService.postCommunication(communication)
        studentService.addCommunicationToStudent(studentId, communication)
        return id
    }
}
