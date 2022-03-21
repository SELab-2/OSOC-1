package be.osoc.team1.backend.security

object ConfigUtil {
    // everyone can access following urls
    val urlsOpenToAll: Array<String> = arrayOf("/", "/login", "/logout", "/error")

    // everyone can post to following urls, this is needed to register a new user
    val urlsOpenToAllToPostTo: Array<String> = arrayOf("/users")
}
