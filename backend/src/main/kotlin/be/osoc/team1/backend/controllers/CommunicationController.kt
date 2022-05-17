package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Communication
import be.osoc.team1.backend.entities.CommunicationDTO
import be.osoc.team1.backend.services.CommunicationService
import be.osoc.team1.backend.services.StudentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/{edition}/communications")
class CommunicationController(
    communicationService: CommunicationService,
    private val studentService: StudentService
) : BaseController<Communication, UUID>(communicationService) {

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
    @Secured("ROLE_COACH")
    @SecuredEdition
    fun createCommunication(
        @PathVariable studentId: UUID,
        @PathVariable edition: String,
        @RequestBody communicationRegister: CommunicationDTO
    ): ResponseEntity<Communication> {
        val communication =
            Communication(
                communicationRegister.message,
                communicationRegister.type,
                edition,
                studentService.getStudentById(studentId, edition)
            )
        studentService.addCommunicationToStudent(communication, edition)
        return getObjectCreatedResponse(communication.id, communication)
    }

    @DeleteMapping("/{communicationId}")
    @Secured("ROLE_COACH")
    @SecuredEdition
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun deleteCommunication(@PathVariable communicationId: UUID, @PathVariable edition: String) =
        studentService.removeCommunicationFromStudent(communicationId, edition)
}
