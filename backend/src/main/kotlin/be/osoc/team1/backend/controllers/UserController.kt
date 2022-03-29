package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.security.TokenUtil
import be.osoc.team1.backend.security.TokenUtil.createAccessAndRefreshToken
import be.osoc.team1.backend.security.TokenUtil.createToken
import be.osoc.team1.backend.security.TokenUtil.decodeAndVerifyToken
import be.osoc.team1.backend.security.TokenUtil.getTokenFromRequest
import be.osoc.team1.backend.services.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
import java.util.HashMap
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
     * containing the url to the updated resource.
     */
    @PatchMapping("/{id}")
    @Secured("ROLE_ADMIN")
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
    @Secured("ROLE_ADMIN")
    fun deleteUser(@PathVariable id: UUID) = service.deleteUserById(id)

    /**
     * Register a new [User]. The created user will be returned. The response will also contain a
     * Location header containing the url to the newly created resource.
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun postUser(@RequestBody userRegistration: User): ResponseEntity<User> {
        val createdUser = service.registerUser(userRegistration.username, userRegistration.email, userRegistration.password)
        return getObjectCreatedResponse(createdUser.id, createdUser)
    }

    /**
     * Change the role of a user with [id] to be [role]. If the user does not exist a 404 will be returned.
     */
    @PostMapping("/{id}/role")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun postUserRole(@PathVariable id: UUID, @RequestBody role: Role) = service.changeRole(id, role)

    /**
     * Get a new access token using your refresh token
     */
    @PostMapping("/refresh")
    fun renewAccessToken(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val refreshToken: String? = getTokenFromRequest(request)
            if (refreshToken != null) {
                val decodedToken = decodeAndVerifyToken(refreshToken)
                val email: String = decodedToken.subject
                val role: String = decodedToken.getClaim("role").asString()

                createAccessAndRefreshToken(response, email, role, refreshToken)
            }
        } catch (exception: Exception) {
            throw exception
        }
    }
}
