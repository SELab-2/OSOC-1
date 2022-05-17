package be.osoc.team1.backend.util

import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * This is an abstract class containing shared code between our serializers. This allows us to work with a much
 * simpler constructor for all other serializers and makes it so that the [baseUrl] code is only in one place.
 */
abstract class BaseSerializer<T>(t: Class<T>?) : StdSerializer<T>(t) {
    constructor() : this(null)

    val scheme = System.getenv("OSOC_SCHEME")?: "http"
    val baseUrl: String = ServletUriComponentsBuilder.fromCurrentContextPath().scheme(scheme).build().toUriString()

    companion object {
        private const val serialVersionUID = 1L
    }
}
