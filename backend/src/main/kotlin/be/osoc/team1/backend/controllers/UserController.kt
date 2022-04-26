package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(private val service: UserService) {
    /**
     * Get all [User] objects stored in the database.
     */
    @GetMapping
    @Secured("ROLE_COACH")
    fun getAllUsers() = service.getAllUsers()

    /**
     * Get a [User] object using their [id]. If the user does not exist 404 will be returned.
     */
    @GetMapping("/{id}")
    @Secured("ROLE_COACH")
    fun getUserById(@PathVariable id: UUID): User = service.getUserById(id)

    /**
     * Update a user, if the user does not exist yet a 404 will be returned. The response will contain a Location header
     * containing the url to the updated resource and the created object in the body.
     */
    @PatchMapping("/{id}")
    @Secured("ROLE_ADMIN")
    fun patchUser(@PathVariable id: UUID, @RequestBody user: User): ResponseEntity<User> {
        if (id != user.id)
            throw FailedOperationException("Request url id=\"$id\" did not match request body id=\"${user.id}\"")

        val updatedUser = service.patchUser(user)
        return getObjectCreatedResponse(updatedUser.id, updatedUser, HttpStatus.OK)
    }

    /**
     * Delete user with [id]. If the user does not exist a 404 will be returned.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun deleteUser(@PathVariable id: UUID) =
        service.deleteUserById(id)

    /**
     * Register a new [User]. The created user will be returned. The response will also contain a
     * Location header containing the url to the newly created resource.
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun postUser(@RequestBody userRegistration: User): ResponseEntity<User> {
        val createdUser = service.registerUser(userRegistration)
        return getObjectCreatedResponse(createdUser.id, createdUser)
    }

    /**
     * Change the role of a user with [id] to be [role]. If the user does not exist a 404 will be returned.
     * Attempting to demote the last remaining [Role.Admin] [User] will result in a 403.
     */
    @PostMapping("/{id}/role")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun postUserRole(@PathVariable id: UUID, @RequestBody role: Role) =
        service.changeRole(id, role)
}
