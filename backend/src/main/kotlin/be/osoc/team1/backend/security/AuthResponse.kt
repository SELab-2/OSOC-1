package be.osoc.team1.backend.security

import be.osoc.team1.backend.entities.EntityViews
import be.osoc.team1.backend.entities.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import java.util.Date
import javax.servlet.http.HttpServletResponse

open class NewTokenResponseData(
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenTTL: Date
) {
    fun addDataToHttpResponse(response: HttpServletResponse) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        ObjectMapper().writerWithView(EntityViews.Public::class.java).writeValue(response.outputStream, this)
    }
}

class AuthResponseData(
    data: NewTokenResponseData,
    val user: User
) : NewTokenResponseData(data.accessToken, data.refreshToken, data.refreshTokenTTL)
