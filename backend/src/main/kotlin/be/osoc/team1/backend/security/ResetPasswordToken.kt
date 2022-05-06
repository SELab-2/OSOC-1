package be.osoc.team1.backend.security

data class ResetPasswordToken(
    val emailAddress: String,
    val ttl: Long = System.currentTimeMillis() + 20 * 60 * 1000 // 20 minutes
) {
    fun isExpired(): Boolean {
        return ttl < System.currentTimeMillis()
    }
}
