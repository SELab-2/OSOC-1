package be.osoc.team1.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
class BackendApplication

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}

/**
 * passwords sent over network are encoded
 */
@Bean
fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder()
}
