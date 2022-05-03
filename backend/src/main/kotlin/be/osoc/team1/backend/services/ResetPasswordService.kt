package be.osoc.team1.backend.services

import be.osoc.team1.backend.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class ResetPasswordService(private val repository: UserRepository) {
}