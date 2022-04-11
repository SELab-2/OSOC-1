package be.osoc.team1.backend.security

/**
 * define which urls don't need authentication/authorization
 */
object ConfigUtil {
    val urlsOpenToAll: Array<String> = arrayOf("/", "/login", "/logout", "/error")

    val urlsOpenToAllToPostTo: Array<String> = arrayOf("/*/users", "/token/refresh")

    val allowedCorsOrigins: List<String> = listOf("http://localhost:3000", "https://sel2-1.ugent.be")
}
