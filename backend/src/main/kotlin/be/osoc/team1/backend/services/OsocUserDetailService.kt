package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.security.SecurityConfig
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.Principal

/**
 * This service implements the [UserDetailsService] interface used by [SecurityConfig]. This allows the authentication
 * system to lookup users and get their authorities.
 */
@Service
class OsocUserDetailService(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder) :
    UserDetailsService {

    /**
     * Get the [User] object from a principal. We use !! here because a user that has successfully logged in has to
     * exist.
     */
    fun getUserFromPrincipal(principal: Principal): User {
        return userRepository.findByEmail(principal.name)!!
    }

    /**
     * Get [UserDetails] for a specific user identified by in our case their [email]. The function name might be
     * confusing at first, but we cannot change it as we are implementing this function from UserDetailService.
     */
    override fun loadUserByUsername(email: String): UserDetails {
        val osocUser: User = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User with email=\"$email\" not found!")

        val authorities = mutableListOf<SimpleGrantedAuthority>()
        for (role in Role.values()) {
            if (osocUser.role.hasPermissionLevel(role)) {
                authorities.add(SimpleGrantedAuthority("ROLE_${role.toString().uppercase()}"))
            }
        }
        return org.springframework.security.core.userdetails.User(
            osocUser.email,
            osocUser.password,
            authorities
        )
    }
}
