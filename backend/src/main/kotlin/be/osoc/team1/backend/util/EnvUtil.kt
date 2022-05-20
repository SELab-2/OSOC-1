package be.osoc.team1.backend.util

object EnvUtil {
    val osocScheme = System.getenv("OSOC_SCHEME") ?: "https"
    val osocUrl = System.getenv("OSOC_URL") ?: "sel2-1.ugent.be"
    val serializerScheme = System.getenv("OSOC_SCHEME") ?: "http"
    val osocEmailAddressSender: String? = System.getenv("OSOC_GMAIL_ADDRESS")
    val osocPasswordSender: String? = System.getenv("OSOC_GMAIL_APP_PASSWORD")
}
