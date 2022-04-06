package be.osoc.team1.backend.security

import be.osoc.team1.backend.entities.User

/**
 * This class does the same as [NewTokenResponseData], but it also holds the authenticated user.
 */
class AuthResponseData(
    data: NewTokenResponseData,
    val user: User
) : NewTokenResponseData(data.accessToken, data.refreshToken, data.refreshTokenTTL)
