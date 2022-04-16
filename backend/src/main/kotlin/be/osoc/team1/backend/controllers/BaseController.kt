package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.services.BaseService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

open class BaseController<T>(open val service: BaseService<T>) {

    /**
     * Returns the T with the corresponding [id]. If no such T exists, returns a
     * "404: Not Found" message instead.
     */
    @GetMapping("/{id}")
    @Secured("ROLE_COACH")
    fun getById(@PathVariable id: UUID): T = service.getById(id)
}
