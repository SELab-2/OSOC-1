package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.exceptions.UnauthorizedOperationException
import be.osoc.team1.backend.services.EditionService
import be.osoc.team1.backend.services.OsocUserDetailService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
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
import java.lang.annotation.Inherited
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

fun attemptEditionAccess(
    editionName: String,
    editionService: EditionService,
    userDetailService: OsocUserDetailService
) {
    val edition = editionService.getEdition(editionName)
    val authentication = SecurityContextHolder.getContext().authentication
    val user = userDetailService.getUserFromPrincipal(authentication)
    if (!edition.accessibleBy(user))
        throw UnauthorizedOperationException("Inactive editions can only be accessed by admins!")
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
        return super.preHandle(request, response, handler)
        // URLs that are always allowed
        val regex =
            Regex("^.*/api/(error|editions|login|communications|users|assignments|positions|statusSuggestions|answers|skills|logout|token).*$")
        // this !is check is here so invalid requests (such as GETs to endpoints that don't exist) still get handled regularly
        if (!regex.matches(request.requestURI) && handler !is ResourceHttpRequestHandler) {
            val roles = SecurityContextHolder.getContext().authentication.authorities.map { it.toString() }
            SecurityContextHolder.getContext().authentication as Principal
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

@Aspect
@Component
class EditionSecurityAspect(val editionService: EditionService, val userDetailService: OsocUserDetailService) {

    @Before(value = "@annotation(SecuredEdition)")
    @Throws(Throwable::class)
    fun validateEditionArgument(joinPoint: JoinPoint): Any {
        val method = (joinPoint.signature as MethodSignature).method
        val securedEdition = method.annotations.find { SecuredEdition::class.java.isInstance(it) } as SecuredEdition
        val editionFieldIndex = method.parameters.indexOfFirst { it.name == securedEdition.editionArgument }
        if (editionFieldIndex < 0)
            throw IllegalStateException("The specified @SecuredEdition editionArgument was not found!")
        val editionName = joinPoint.args[editionFieldIndex] as String

        attemptEditionAccess(editionName, editionService, userDetailService)

        return joinPoint
    }
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited //Doesn't work in kotlin...
annotation class SecuredEdition(val editionArgument: String)
