package be.osoc.team1.backend.security

import com.auth0.jwt.algorithms.Algorithm

data class SecretUtil(val algorithm: Algorithm = Algorithm.HMAC256("secret"))
