package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.EditionController
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.services.EditionService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@UnsecuredWebMvcTest(EditionController::class)
class EditionControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var editionService: EditionService

    private val testEditionName = "edition"

    @Test
    fun `makeEditionInactive returns nothing when it succeeds`() {
        every { editionService.makeEditionInactive(any()) } just Runs
        mockMvc.perform(post("/$testEditionName/inactivate"))
            .andExpect(status().isOk)
            .andExpect(content().string(""))
    }

    @Test
    fun `makeEditionInactive returns 400 (BAD REQUEST) if the edition was already inactive`() {
        every { editionService.makeEditionInactive(any()) }.throws(FailedOperationException())
        mockMvc.perform(post("/$testEditionName/inactivate")).andExpect(status().isBadRequest)
    }

    @Test
    fun `makeEditionActive returns nothing when it succeeds`() {
        every { editionService.makeEditionActive(any()) } just Runs
        mockMvc.perform(post("/$testEditionName/activate"))
            .andExpect(status().isOk)
            .andExpect(content().string(""))
    }

    @Test
    fun `makeEditionActive returns 400 (BAD REQUEST) if the edition was already active`() {
        every { editionService.makeEditionActive(any()) }.throws(FailedOperationException())
        mockMvc.perform(post("/$testEditionName/activate")).andExpect(status().isBadRequest)
    }

    @Test
    fun `makeEditionActive returns 403 (FORBIDDEN) if there is already another active edition`() {
        every { editionService.makeEditionActive(any()) }.throws(ForbiddenOperationException())
        mockMvc.perform(post("/$testEditionName/activate")).andExpect(status().isForbidden)
    }
}