package be.osoc.team1.backend.security

import be.osoc.team1.backend.entities.EntityViews
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import java.util.Date
import javax.servlet.http.HttpServletResponse

/**
 * This class contains all the data that will be added to the response the user gets after acquiring a new access token.
 * This class can get overridden by [AuthResponseData].
 */
open class NewTokenResponseData(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenTTL: Date
) {
    /**
     * Add data of this class to [response].
     */
    fun addDataToHttpResponse(response: HttpServletResponse) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        ObjectMapper().writerWithView(EntityViews.Public::class.java).writeValue(response.outputStream, this)
    }
}
