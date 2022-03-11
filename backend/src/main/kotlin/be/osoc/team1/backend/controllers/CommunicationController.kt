package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.services.CommunicationService
import be.osoc.team1.backend.services.StudentService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
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
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun createCommunication(@PathVariable studentId: UUID, @RequestBody communication: Communication): ResponseEntity<Void> {
        val id = communicationService.createCommunication(communication)
        studentService.addCommunicationToStudent(studentId, communication)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUriString()
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, location).build()
    }
}
