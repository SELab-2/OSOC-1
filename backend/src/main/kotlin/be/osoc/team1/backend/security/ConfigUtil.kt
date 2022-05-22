package be.osoc.team1.backend.security

/**
 * Configure which urls are excluded from the standard security measures. Those standard security measures are defined
 * in [SecurityConfig].
 */
object ConfigUtil {
    val urlsOpenToAll: Array<String> = arrayOf(
        "/", "/login", "/logout", "/error", "/forgotPassword/*"
    )
    val urlsOpenToAllToPostTo: Array<String> = arrayOf(
        "/users", "/forgotPassword", "/token/refresh", "/*/students"
    )
    val allowedCorsOrigins: List<String> = listOf(System.getenv("OSOC_FRONTEND_URL") ?: "http://localhost:3000")
}
