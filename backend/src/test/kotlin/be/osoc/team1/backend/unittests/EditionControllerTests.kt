package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.controllers.EditionController
import be.osoc.team1.backend.entities.Edition
import be.osoc.team1.backend.exceptions.FailedOperationException
import be.osoc.team1.backend.exceptions.ForbiddenOperationException
import be.osoc.team1.backend.exceptions.InvalidIdException
import be.osoc.team1.backend.services.EditionService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@UnsecuredWebMvcTest(EditionController::class)
class EditionControllerTests(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var editionService: EditionService

    private val editionName = "edition"

    @Test
    fun `getEdition returns the edition if it exists`() {
        val edition = Edition(editionName, true)
        // We can't use Jackson here, as it will rename the 'isActive' field to 'active' for some reason.
        // See: https://stackoverflow.com/questions/32270422/jackson-renames-primitive-boolean-field-by-removing-is
        val expected = "{\"name\":\"$editionName\",\"isActive\":true}"
        every { editionService.getEdition(editionName) } returns edition
        mockMvc.perform(get("/$editionName"))
            .andExpect(status().isOk)
            .andExpect(content().string(expected))
    }

    @Test
    fun `getEdition returns 404 (NOT FOUND) if the edition does not exist`() {
        every { editionService.getEdition(editionName) }.throws(InvalidIdException())
        mockMvc.perform(get("/$editionName")).andExpect(status().isNotFound)
    }

    @Test
    fun `getActiveEdition returns the active edition if it exists`() {
        val activeEdition = Edition(editionName, true)
        val expected = "{\"name\":\"$editionName\",\"isActive\":true}"
        every { editionService.getActiveEdition() } returns activeEdition
        mockMvc.perform(get("/editions/active"))
            .andExpect(status().isOk)
            .andExpect(content().string(expected))
    }

    @Test
    fun `getActiveEdition returns nothing if there is no active edition`() {
        every { editionService.getActiveEdition() } returns null
        mockMvc.perform(get("/editions/active"))
            .andExpect(status().isOk)
            .andExpect(content().string(""))
    }

    @Test
    fun `getInactiveEditions returns all inactive editions`() {
        val inactiveEdition1 = Edition("edition1", false)
        val inactiveEdition2 = Edition("edition2", false)
        val expected = "[{\"name\":\"edition1\",\"isActive\":false},{\"name\":\"edition2\",\"isActive\":false}]"
        every { editionService.getInactiveEditions() } returns listOf(inactiveEdition1, inactiveEdition2)
        mockMvc.perform(get("/editions/inactive"))
            .andExpect(status().isOk)
            .andExpect(content().string(expected))
    }

    @Test
    fun `makeEditionInactive returns nothing when it succeeds`() {
        every { editionService.makeEditionInactive(editionName) } just Runs
        mockMvc.perform(post("/$editionName/inactivate"))
            .andExpect(status().isOk)
            .andExpect(content().string(""))
    }

    @Test
    fun `makeEditionInactive returns 400 (BAD REQUEST) if the edition is already inactive`() {
        every { editionService.makeEditionInactive(editionName) }.throws(FailedOperationException())
        mockMvc.perform(post("/$editionName/inactivate")).andExpect(status().isBadRequest)
    }

    @Test
    fun `makeEditionActive returns nothing when it succeeds`() {
        every { editionService.makeEditionActive(editionName) } just Runs
        mockMvc.perform(post("/$editionName/activate"))
            .andExpect(status().isOk)
            .andExpect(content().string(""))
    }

    @Test
    fun `makeEditionActive returns 400 (BAD REQUEST) if the edition is already active`() {
        every { editionService.makeEditionActive(editionName) }.throws(FailedOperationException())
        mockMvc.perform(post("/$editionName/activate")).andExpect(status().isBadRequest)
    }

    @Test
    fun `makeEditionActive returns 403 (FORBIDDEN) if there is already another active edition`() {
        every { editionService.makeEditionActive(editionName) }.throws(ForbiddenOperationException())
        mockMvc.perform(post("/$editionName/activate")).andExpect(status().isForbidden)
    }

    @Test
    fun `deleteEdition returns 204 (NO CONTENT) if the edition existed`() {
        every { editionService.deleteEdition(editionName) } just Runs
        mockMvc.perform(delete("/$editionName")).andExpect(status().isNoContent)
    }

    @Test
    fun `deleteEdition returns 404 (NOT FOUND) if the edition did not exist`() {
        every { editionService.deleteEdition(editionName) }.throws(InvalidIdException())
        mockMvc.perform(delete("/$editionName")).andExpect(status().isNotFound)
    }
}
