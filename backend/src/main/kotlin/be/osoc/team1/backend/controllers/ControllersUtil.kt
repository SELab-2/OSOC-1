package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.OsocUserDetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.security.Principal
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
fun <ID, T> getObjectCreatedResponse(id: ID, createdObject: T, status: HttpStatus = HttpStatus.CREATED): ResponseEntity<T> {
    val postRequestPath = ServletUriComponentsBuilder.fromCurrentRequest()
    val pathWithIdAdded = postRequestPath.path("/{id}").buildAndExpand(id).toUriString()
    return ResponseEntity
        .status(status)
        .header(HttpHeaders.LOCATION, pathWithIdAdded)
        .body(createdObject)
}

@Component
class TestingInterceptor(val editionService: EditionService, val userDetailService: OsocUserDetailService) : HandlerInterceptor {
    @Throws(Exception::class)
    @Override
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (request.requestURI != "/api/error") {
            println(request.requestURI)
            println("ese")
            println(handler)
            response.addHeader("WIE", "WOO")
            response.sendError(411, "test")
            println(editionService.getActiveEdition())
            return false
        }
        return super.preHandle(request, response, handler)

        // set few parameters to handle ajax request from different host
        response.addHeader("Access-Control-Allow-Origin", "*")
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS")
        response.addHeader("Access-Control-Max-Age", "1000")
        response.addHeader("Access-Control-Allow-Headers", "Content-Type")
        response.addHeader("Cache-Control", "private")
        val reqUri = request.requestURI
        val serviceName = reqUri.substring(
            reqUri.lastIndexOf("/") + 1,
            reqUri.length
        )
        if (serviceName == "SOMETHING") {
        }
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
class ProductServiceInterceptorAppConfig : WebMvcConfigurer {
    @Autowired
    lateinit var testServiceInterceptor: TestingInterceptor
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(testServiceInterceptor)
    }
}