package be.osoc.team1.backend.util

import org.springframework.web.servlet.support.ServletUriComponentsBuilder

object BaseUrlUtil {
    fun getBaseUrl(): String {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
    }
}
