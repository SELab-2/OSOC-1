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
     * Gets all communications that belong to a student, if this [studentId] doesn't exist the service will return a 404
     */
    @GetMapping("/{studentId}")
    fun getCommunicationsByStudentId(@PathVariable studentId: UUID): Collection<Communication> =
        studentService.getStudentById(studentId).communications

    /**
     * Add a communication to the database. The communication should be passed in the request body
     * as a JSON object and should have the following format:
     *
     * ```
     * {
     *     "message": "(INSERT MESSAGE)",
     *     "type": "Email"
     * }
     * ```
     *
     * The location of the newly created communication is then returned to the API caller in the location header.
     * Note that the type can be any of the types defined in [Communication]
     */
    @PostMapping("/{studentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun createCommunication(@PathVariable studentId: UUID, @RequestBody communication: Communication): ResponseEntity<Void> {
        val id = communicationService.createCommunication(communication)
        communication.id = id
        studentService.addCommunicationToStudent(studentId, communication)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUriString()
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, location).build()
    }
}
