package be.osoc.team1.backend.security

/**
 * Configure which urls are excluded from the standard security measures. Those standard security measures are defined
 * in [SecurityConfig].
 */
object ConfigUtil {
    val urlsOpenToAll: Array<String> = arrayOf("/login", "/error", "/token/refresh")

    val urlsOpenToLoggedInUsers: Array<String> = arrayOf("/token/logout")

    val urlsOpenToAllToPostTo: Array<String> = arrayOf("/*/users")

    val allowedCorsOrigins: List<String> = listOf("http://localhost:3000", "https://sel2-1.ugent.be")
}
