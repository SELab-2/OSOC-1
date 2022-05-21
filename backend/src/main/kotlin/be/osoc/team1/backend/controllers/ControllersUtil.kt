package be.osoc.team1.backend.controllers

import be.osoc.team1.backend.entities.StudentView
import be.osoc.team1.backend.entities.StudentViewEnum
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.lang.annotation.Inherited
import javax.servlet.http.HttpServletRequest

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

/**
 * This function checks if a request is allowed based on the edition of the accessed resource ([editionName]),
 * (entries of inactive editions can only be viewed or deleted by admins).
 * [editionService], [userDetailService] and [httpServletRequest] should be injected in the caller of this function.
 */
fun attemptEditionAccess(
    editionName: String,
    editionService: EditionService,
    userDetailService: OsocUserDetailService,
    httpServletRequest: HttpServletRequest
) {
    val edition = editionService.getEdition(editionName)
    val authentication = SecurityContextHolder.getContext().authentication
    val user = userDetailService.getUserFromPrincipal(authentication)

    if (!edition.accessibleBy(user))
        throw UnauthorizedOperationException("Entries of inactive editions can only be accessed by admins!")
    if (!edition.isActive && httpServletRequest.method != "GET" && httpServletRequest.method != "DELETE")
        throw ForbiddenOperationException("Entries of inactive editions can only be viewed or deleted (Allowed methods: GET, DELETE)")
}

/**
 * This class adds the code for the SecuredEdition annotation
 */
@Aspect
@Component
class EditionSecurityAspect(val editionService: EditionService, val userDetailService: OsocUserDetailService) {

    @Autowired
    private lateinit var request: HttpServletRequest

    @Before(value = "@annotation(SecuredEdition)")
    @Throws(Throwable::class)
    fun validateEditionArgument(joinPoint: JoinPoint): Any {
        val method = (joinPoint.signature as MethodSignature).method
        val editionFieldIndex = method.parameters.indexOfFirst { it.name == "edition" }
        if (editionFieldIndex < 0)
            throw IllegalStateException("The @SecuredEdition edition argument was not found! (With this annotation the function needs an edition argument)")
        val editionName = joinPoint.args[editionFieldIndex] as String
        attemptEditionAccess(editionName, editionService, userDetailService, request)
        return joinPoint
    }
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class SecuredEdition

fun studentViewEnumToStudentView(viewEnum: StudentViewEnum): Class<out StudentView.Basic> {
    return when (viewEnum) {
        StudentViewEnum.Full -> StudentView.Full::class.java
        StudentViewEnum.Basic -> StudentView.Basic::class.java
        StudentViewEnum.List -> StudentView.List::class.java
        StudentViewEnum.Extra -> StudentView.Extra::class.java
        StudentViewEnum.Communication -> StudentView.Communication::class.java
    }
}
