package be.osoc.team1.backend.security

import be.osoc.team1.backend.util.EnvUtil.osocScheme
import be.osoc.team1.backend.util.EnvUtil.osocUrl

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
    val allowedCorsOrigins: List<String> = listOf("http://localhost:3000", "$osocScheme://$osocUrl")
}
