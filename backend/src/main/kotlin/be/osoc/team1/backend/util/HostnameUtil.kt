package be.osoc.team1.backend.util

import java.lang.System.getProperty

class HostnameUtil {
    fun getHostname(): String {
        return getProperty("OSOC_HOSTNAME", "spring.datasource.hostname")
    }
}