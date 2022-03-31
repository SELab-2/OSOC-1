package be.osoc.team1.backend.security

/**
 * define which urls don't need authentication/authorization
 */
object ConfigUtil {
    val urlsOpenToAll: Array<String> = arrayOf("/", "/login", "/logout", "/error")

    val urlsOpenToAllToPostTo: Array<String> = arrayOf("/*/users")
}
