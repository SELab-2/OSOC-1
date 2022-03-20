package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.EditionController
import be.osoc.team1.backend.services.EditionService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(EditionController::class)
class EditionControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var editionService: EditionService

    @Test
    fun `createNewEdition should not fail`() {
        every { editionService.createNewEdition() } just Runs
        mockMvc.perform(post("/edition/new")).andExpect(status().isNoContent)
    }
}
