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
    val osocScheme = System.getenv("OSOC_SCHEME") ?: "http"
    private val osocUrl = System.getenv("OSOC_URL") ?: "localhost:3000"
    val allowedCorsOrigins: List<String> = listOf("$osocScheme://$osocUrl")
}
