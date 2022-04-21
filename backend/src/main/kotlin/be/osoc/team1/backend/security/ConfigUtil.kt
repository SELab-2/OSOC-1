package be.osoc.team1.backend.security

/**
 * Configure which urls are excluded from the standard security measures. Those standard security measures are defined
 * in [SecurityConfig].
 */
object ConfigUtil {
    val urlsOpenToAll: Array<String> = arrayOf("/", "/login", "/logout", "/error")
    val urlsOpenToAllToPostTo: Array<String> = arrayOf("/users", "/token/refresh", "/students")
    val allowedCorsOrigins: List<String> = listOf("http://localhost:3000", "https://sel2-1.ugent.be")
}
