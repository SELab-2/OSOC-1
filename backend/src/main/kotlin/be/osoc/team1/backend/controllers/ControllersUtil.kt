package be.osoc.team1.backend.controllers

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * Utility method that takes an object that was just created by a service method and it's [id],
 * and returns a [ResponseEntity] with the [HttpStatus.CREATED] status containing the [createdObject] in the body.
 * Additionally, the location header will be set to the path of the POST request that requested the creation
 * of the object, with the given [id] added at the end.
 *
 * For example, if the path to the POST request is:
 *
 * `/api/students`
 *
 * Then the location header will contain:
 *
 * `/api/students/(INSERT ID)`
 */
fun <ID, T> getObjectCreatedResponse(id: ID, createdObject: T): ResponseEntity<T> {
    val postRequestPath = ServletUriComponentsBuilder.fromCurrentRequest()
    val pathWithIdAdded = postRequestPath.path("/{id}").buildAndExpand(id).toUriString()
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header(HttpHeaders.LOCATION, pathWithIdAdded)
        .body(createdObject)
}
