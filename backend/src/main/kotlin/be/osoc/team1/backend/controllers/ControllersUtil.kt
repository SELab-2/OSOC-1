package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.OsocUserDetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
fun <ID, T> getObjectCreatedResponse(
    id: ID,
    createdObject: T,
    status: HttpStatus = HttpStatus.CREATED
): ResponseEntity<T> {
    val postRequestPath = ServletUriComponentsBuilder.fromCurrentRequest()
    val pathWithIdAdded = postRequestPath.path("/{id}").buildAndExpand(id).toUriString()
    return ResponseEntity
        .status(status)
        .header(HttpHeaders.LOCATION, pathWithIdAdded)
        .body(createdObject)
}

@Component
class EditionInterceptor(val editionService: EditionService) :
    HandlerInterceptor {
    @Throws(Exception::class)
    @Override
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // URLs that are always allowed
        val regex =
            Regex("^.*/api/(error|editions|login|communications|users|assignments|positions|statusSuggestions|answers|skills|logout|token).*$")
        // this !is check is here so invalid requests (such as GETs to endpoints that don't exist) still get handled regularly
        if (!regex.matches(request.requestURI) && handler !is ResourceHttpRequestHandler) {
            val roles = SecurityContextHolder.getContext().authentication.authorities.map { it.toString() }
            val editionName = request.requestURI.split("/")[2]
            if (editionService.getActiveEdition()?.name != editionName) {
                if (!roles.contains("ROLE_ADMIN")) {
                    response.sendError(401, "Inactive editions can only be accessed by admins")
                    return false
                }
                if (request.method != "GET" && request.method != "DELETE") {
                    response.sendError(405, "Entries from inactive editions can only be viewed or deleted")
                    return false
                }
            }
        }
        return super.preHandle(request, response, handler)
    }

    @Throws(Exception::class)
    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse, handler: Any,
        modelAndView: ModelAndView?
    ) {
        super.postHandle(request, response, handler, modelAndView)
    }
}

@Component
class InterceptorConfig : WebMvcConfigurer {
    @Autowired
    lateinit var editionInterceptor: EditionInterceptor
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(editionInterceptor)
    }
}