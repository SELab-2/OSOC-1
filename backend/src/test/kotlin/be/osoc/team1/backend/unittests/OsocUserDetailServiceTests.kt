package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.services.OsocUserDetailService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import java.nio.file.attribute.UserPrincipal

class OsocUserDetailServiceTests {

    companion object {
        private val testAdmin = User("Admin", "admin@email.com", Role.Admin, "admin")
        private val testCoach = User("Coach", "coach@email.com", Role.Coach, "coach")
        private val testDisabled = User("Admin", "disabled@email.com", Role.Disabled, "disabled")
        private lateinit var userDetailsAdmin: org.springframework.security.core.userdetails.User
        private lateinit var userDetailsCoach: org.springframework.security.core.userdetails.User
        private lateinit var userDetailsDisabled: org.springframework.security.core.userdetails.User
        private val authorities = mutableListOf<SimpleGrantedAuthority>()

        @BeforeAll
        @JvmStatic
        fun createUserDetails() {
            authorities.add(SimpleGrantedAuthority("ROLE_${Role.Disabled.toString().uppercase()}"))
            userDetailsDisabled = org.springframework.security.core.userdetails.User(testDisabled.email, testDisabled.password, authorities)
            authorities.add(SimpleGrantedAuthority("ROLE_${Role.Coach.toString().uppercase()}"))
            userDetailsCoach = org.springframework.security.core.userdetails.User(testCoach.email, testCoach.password, authorities)
            authorities.add(SimpleGrantedAuthority("ROLE_${Role.Admin.toString().uppercase()}"))
            userDetailsAdmin = org.springframework.security.core.userdetails.User(testAdmin.email, testAdmin.password, authorities)
        }
    }

    private fun getRepository(userAlreadyExists: Boolean): UserRepository {
        val repository: UserRepository = mockk()
        every { repository.findByEmail(testAdmin.email) } returns if (userAlreadyExists) testAdmin else null
        every { repository.findByEmail(testCoach.email) } returns if (userAlreadyExists) testCoach else null
        every { repository.findByEmail(testDisabled.email) } returns if (userAlreadyExists) testDisabled else null
        return repository
    }

    private fun getPasswordEncoder(): PasswordEncoder {
        val passwordEncoder: PasswordEncoder = mockk()
        every { passwordEncoder.encode(any()) } returns "Encoded password"
        return passwordEncoder
    }

    @Test
    fun `loadUserByUsername with empty repo throws UsernameNotFoundException`() {
        val service = OsocUserDetailService(getRepository(false), getPasswordEncoder())
        assertThrows<UsernameNotFoundException> { service.loadUserByUsername(testAdmin.email) }
    }

    @Test
    fun `loadUserByUsername does not fail when the user with email exists and is admin`() {
        val service = OsocUserDetailService(getRepository(true), getPasswordEncoder())
        assert(service.loadUserByUsername(testAdmin.email) == userDetailsAdmin)
    }

    @Test
    fun `loadUserByUsername does not fail when the user with email exists and is coach`() {
        val service = OsocUserDetailService(getRepository(true), getPasswordEncoder())
        assert(service.loadUserByUsername(testCoach.email) == userDetailsCoach)
    }

    @Test
    fun `loadUserByUsername does not fail when the user with email exists and is disabled`() {
        val service = OsocUserDetailService(getRepository(true), getPasswordEncoder())
        assert(service.loadUserByUsername(testDisabled.email) == userDetailsDisabled)
    }

    @Test
    fun `getUserFromPrincipal works when user is present`() {
        val service = OsocUserDetailService(getRepository(true), getPasswordEncoder())
        assert(service.getUserFromPrincipal(UserPrincipal { testAdmin.email }) == testAdmin)
    }
}
