package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.services.UserService
import org.springframework.http.HttpStatus
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
    @GetMapping
    fun getAllUsers() = service.getAllUsers()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID) = service.getUserById(id)

    @PatchMapping("/{id}")
    fun patchUser(@PathVariable id: UUID, @RequestBody user: User) = service.patchUser(user)

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: UUID) = service.deleteUserById(id)

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun postUser(@RequestBody user: User) = service.postUser(user)

    @PostMapping("/{id}/role")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun postUserRole(@PathVariable id: UUID, @RequestBody role: Role) = service.changeRole(id, role)
}
