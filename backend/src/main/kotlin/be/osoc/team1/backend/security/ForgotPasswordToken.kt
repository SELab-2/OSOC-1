package be.osoc.team1.backend.security

/**
 * This class is used in [ForgotPasswordService]. An instance of this class gets created when a user requests to change
 * its password. This user can change its password as long as the token hasn't expired (20 minutes after creation).
 */
data class ForgotPasswordToken(
    val emailAddress: String,
    val ttl: Long = System.currentTimeMillis() + 20 * 60 * 1000
) {
    fun isExpired(): Boolean {
        return ttl < System.currentTimeMillis()
    }
}
