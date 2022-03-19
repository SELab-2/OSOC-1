package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
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
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/users")
class UserController(private val service: UserService, private val passwordEncoder: PasswordEncoder) {
    /**
     * Get all [User] objects stored in the database.
     */
    @GetMapping
    fun getAllUsers() = service.getAllUsers()

    /**
     * Get a [User] object using their [id]. If the user does not exist 404 will be returned.
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): User = service.getUserById(id)

    /**
     * Update a user, if the user does not exist yet a 404 will be returned. The response will contain a Location header
     * containing the url to the updated resource.
     */
    @PatchMapping("/{id}")
    fun patchUser(@PathVariable id: UUID, @RequestBody user: User, request: HttpServletRequest, responseHeader: HttpServletResponse) {
        if (id != user.id)
            throw FailedOperationException("Request url id=\"$id\" did not match request body id=\"${user.id}\"")

        service.patchUser(user)
        responseHeader.addHeader("Location", request.requestURL.toString())
    }

    /**
     * Delete user with [id]. If the user does not exist a 404 will be returned.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: UUID) = service.deleteUserById(id)

    /**
     * Register a new [User]. The id of the new user will be returned. The response will also contain a
     * Location header containing the url to the newly created resource. Newly created users have the [Role.Disabled]
     * role by default.
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun postUser(@RequestBody userRegistration: UserRegistration, request: HttpServletRequest, responseHeader: HttpServletResponse): UUID {
        val user = User(
            userRegistration.username,
            userRegistration.email,
            Role.Disabled,
            passwordEncoder.encode(userRegistration.password)
        )

        val id = service.postUser(user)
        responseHeader.addHeader("Location", request.requestURL.toString() + "/$id")
        return id
    }

    /**
     * Change the role of a user with [id] to be [role]. If the user does not exist a 404 will be returned.
     */
    @PostMapping("/{id}/role")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun postUserRole(@PathVariable id: UUID, @RequestBody role: Role) = service.changeRole(id, role)

    /**
     * Data class used for registering a [User], we have an extra data class for this because newly created users
     * shouldn't have to specify their role if it will be set to [Role.Disabled] anyway.
     */
    data class UserRegistration(val username: String, val email: String, val password: String)
}
