package be.osoc.team1.backend.services

import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class OsocUserDetailService(val userRepository: UserRepository) : UserDetailsService {
    val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    /**
     * Get [UserDetails] for a specific user identified by in our case their [email]. The function name might be
     * confusing at first, but we cannot change it as we are implementing this function from UserDetailService.
     */
    override fun loadUserByUsername(email: String): UserDetails {
        val osocUser: User = userRepository.findByEmail(email)[0]
        return org.springframework.security.core.userdetails.User(
            osocUser.email,
            passwordEncoder.encode(osocUser.password),
            mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
        )
        /*return org.springframework.security.core.userdetails.User(
            "user",
            passwordEncoder.encode("pass"),
            mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
        )*/
    }
}
