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
    private val osocScheme = System.getenv("OSOC_SCHEME") ?: "https"
    private val osocUrl = System.getenv("OSOC_URL") ?: "sel2-1.ugent.be"
    val allowedCorsOrigins: List<String> = listOf("http://localhost:3000", "$osocScheme://$osocUrl")
}
