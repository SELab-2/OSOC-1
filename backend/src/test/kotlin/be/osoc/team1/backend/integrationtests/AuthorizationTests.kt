package be.osoc.team1.backend.integrationtests

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.repositories.UserRepository
import be.osoc.team1.backend.security.ConfigUtil
import be.osoc.team1.backend.security.TokenUtil.decodeAndVerifyToken
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorizationTests() {

    @AfterEach
    fun cleanup() {
        studentRepository.deleteAll()
        userRepository.deleteAll()
    }

    @BeforeEach
    fun insert() {
        userRepository.save(adminUser)
        userRepository.save(coachUser)
        userRepository.save(disabledUser)
        studentRepository.save(testStudent)
    }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private val adminPassword = "adminPassword"
    private val adminEmail = "admin@admin.com"
    private val encodedAdminPassword = BCryptPasswordEncoder().encode(adminPassword)
    private val adminUser = User("admin", adminEmail, Role.Admin, encodedAdminPassword)

    private val coachPassword = "coachPassword"
    private val coachEmail = "coach@coach.com"
    private val encodedCoachPassword = BCryptPasswordEncoder().encode(coachPassword)
    private val coachUser = User("coach", coachEmail, Role.Coach, encodedCoachPassword)

    private val disabledPassword = "disabledPassword"
    private val disabledEmail = "disabled@disabled.com"
    private val encodedDisabledPassword = BCryptPasswordEncoder().encode(disabledPassword)
    private val disabledUser = User("disabled", disabledEmail, Role.Disabled, encodedDisabledPassword)

    private val testStudent = Student("Test", "Student")

    /**
     * Log in with given email and password via post request to /login
     */
    fun loginUser(email: String, password: String): ResponseEntity<String> {
        val input = "email=$email&password=$password"
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginRequest = HttpEntity(input, loginHeaders)

        return restTemplate.exchange(URI("/login"), HttpMethod.POST, loginRequest, String::class.java)
    }

    /**
     * Create the correct header for an access token to perform an authenticated request
     */
    fun createAuthHeaders(accessToken: String): HttpHeaders {
        val authHeaders = HttpHeaders()
        authHeaders.add("Authorization", "Basic $accessToken")
        return authHeaders
    }

    /**
     * Use [refreshToken] to get a new access token.
     */
    fun requestNewAccessToken(refreshToken: String): ResponseEntity<String> {
        val input = "refreshToken=$refreshToken"
        val refreshHeaders = HttpHeaders()
        refreshHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val refreshRequest = HttpEntity(input, refreshHeaders)
        return restTemplate.exchange(URI("/token/refresh"), HttpMethod.POST, refreshRequest, String::class.java)
    }

    /**
     * Logout to avoid influencing other tests
     */
    fun logoutResponse(response: ResponseEntity<String>) {
        val accessToken: String = JSONObject(response.body).get("accessToken") as String
        logoutHeader(createAuthHeaders(accessToken))
    }

    /**
     * Logout to avoid influencing other tests
     */
    fun logoutHeader(authHeaders: HttpHeaders) {
        val request = HttpEntity(null, authHeaders)
        restTemplate.exchange(URI("/logout"), HttpMethod.POST, request, String::class.java)
    }

    /**
     * Create a header that can be used for further requests based upon given credentials
     */
    fun getAuthenticatedHeader(email: String, password: String): HttpHeaders {
        val response: ResponseEntity<String> = loginUser(email, password)
        val accessToken: String = JSONObject(response.body).get("accessToken") as String
        return createAuthHeaders(accessToken)
    }

    @Test
    fun `login with no credentials returns 401`() {
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginRequest = HttpEntity("", loginHeaders)
        val loginResponse: ResponseEntity<String> = restTemplate.exchange(URI("/login"), HttpMethod.POST, loginRequest, String::class.java)
        assert(loginResponse.statusCodeValue == 401)
    }

    @Test
    fun `login with only email returns 401`() {
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginRequest = HttpEntity("email=admin@admin.com", loginHeaders)
        val loginResponse: ResponseEntity<String> = restTemplate.exchange(URI("/login"), HttpMethod.POST, loginRequest, String::class.java)
        assert(loginResponse.statusCodeValue == 401)
    }

    @Test
    fun `login with only password returns 401`() {
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginRequest = HttpEntity("password=adminPassword", loginHeaders)
        val loginResponse: ResponseEntity<String> = restTemplate.exchange(URI("/login"), HttpMethod.POST, loginRequest, String::class.java)
        assert(loginResponse.statusCodeValue == 401)
    }

    @Test
    fun `login works as admin`() {
        val loginResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        assert(loginResponse.statusCodeValue == 200)
        assert(JSONObject(loginResponse.body).has("accessToken"))
        assert(JSONObject(loginResponse.body).has("refreshToken"))
        logoutResponse(loginResponse)
    }

    @Test
    fun `login works as coach`() {
        val loginResponse: ResponseEntity<String> = loginUser(coachEmail, coachPassword)
        assert(loginResponse.statusCodeValue == 200)
        assert(JSONObject(loginResponse.body).has("accessToken"))
        assert(JSONObject(loginResponse.body).has("refreshToken"))
        logoutResponse(loginResponse)
    }

    @Test
    fun `login works as disabled`() {
        val loginResponse: ResponseEntity<String> = loginUser(disabledEmail, disabledPassword)
        assert(loginResponse.statusCodeValue == 200)
        assert(JSONObject(loginResponse.body).has("accessToken"))
        assert(JSONObject(loginResponse.body).has("refreshToken"))
        logoutResponse(loginResponse)
    }

    @Test
    fun `wrong authorization header returns 403`() {
        val loginResponse: ResponseEntity<String> = loginUser(disabledEmail, disabledPassword)
        val accessToken: String = JSONObject(loginResponse.body).get("accessToken") as String
        val authHeaders = HttpHeaders()
        authHeaders.add("Authorization", "Invalid $accessToken")
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)
        assert(response.statusCodeValue == 403)
        logoutResponse(loginResponse)
    }

    @Test
    fun `access token can be used after login`() {
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)
        assert(response.statusCodeValue == 200)
        logoutHeader(authHeaders)
    }

    @Test
    fun `GET students returns 403 when not logged in`() {
        val response: ResponseEntity<String> = restTemplate.getForEntity<String>("/students")
        assert(response.statusCodeValue == 403)
    }

    @Test
    fun `GET students works when logged in as admin`() {
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)

        assert(response.statusCodeValue == 200)
        assert(JSONArray(response.body).getJSONObject(0).get("firstName") == testStudent.firstName)
        assert(JSONArray(response.body).getJSONObject(0).get("lastName") == testStudent.lastName)
        logoutHeader(authHeaders)
    }

    @Test
    fun `GET students works when logged in as coach`() {
        val authHeaders = getAuthenticatedHeader(coachEmail, coachPassword)
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)

        assert(response.statusCodeValue == 200)
        assert(JSONArray(response.body).getJSONObject(0).get("firstName") == testStudent.firstName)
        assert(JSONArray(response.body).getJSONObject(0).get("lastName") == testStudent.lastName)
        logoutHeader(authHeaders)
    }

    @Test
    fun `GET students returns 403 when logged in as disabled`() {
        val authHeaders = getAuthenticatedHeader(disabledEmail, disabledPassword)
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)

        assert(response.statusCodeValue == 403)
        logoutHeader(authHeaders)
    }

    @Test
    fun `Authentication with invalid access token returns 401`() {
        val accessToken: String = "in.val.id"
        val request = HttpEntity(null, createAuthHeaders(accessToken))

        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)
        assert(response.statusCodeValue == 401)
    }

    @Test
    fun `Authentication with refresh token returns 401`() {
        val logInResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        val refreshToken: String = JSONObject(logInResponse.body).get("refreshToken") as String
        val request = HttpEntity(null, createAuthHeaders(refreshToken))

        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)
        assert(response.statusCodeValue == 401)
    }

    @Test
    fun `changing role as admin returns 204`() {
        val userId = coachUser.id
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        authHeaders.add("Content-Type", "application/json")
        val request = HttpEntity("\"Disabled\"", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/users/$userId/role"), HttpMethod.POST, request, String::class.java)

        assert(response.statusCodeValue == 204)
        assert(userRepository.findByEmail(coachUser.email)?.role == Role.Disabled)
        logoutHeader(authHeaders)
    }

    @Test
    fun `changing role as coach returns 403`() {
        val userId = disabledUser.id
        val authHeaders = getAuthenticatedHeader(coachEmail, coachPassword)
        authHeaders.add("Content-Type", "application/json")
        val request = HttpEntity("\"Coach\"", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/users/$userId/role"), HttpMethod.POST, request, String::class.java)

        assert(response.statusCodeValue == 403)
        assert(userRepository.findByEmail(disabledUser.email)?.role == Role.Disabled)
        logoutHeader(authHeaders)
    }

    @Test
    fun `changing role as disabled returns 403`() {
        val userId = coachUser.id
        val authHeaders = getAuthenticatedHeader(disabledEmail, disabledPassword)
        authHeaders.add("Content-Type", "application/json")
        val request = HttpEntity("\"Disabled\"", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/users/$userId/role"), HttpMethod.POST, request, String::class.java)

        assert(response.statusCodeValue == 403)
        assert(userRepository.findByEmail(coachUser.email)?.role == Role.Coach)
        logoutHeader(authHeaders)
    }

    @Test
    fun `changing role as admin on last admin returns 403`() {
        val userId = adminUser.id
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        authHeaders.add("Content-Type", "application/json")
        val request = HttpEntity("\"Disabled\"", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/users/$userId/role"), HttpMethod.POST, request, String::class.java)

        assert(response.statusCodeValue == 403)
        assert(userRepository.findByEmail(adminUser.email)?.role == Role.Admin)
        logoutHeader(authHeaders)
    }

    @Test
    fun `refresh token rotation happens`() {
        val logInResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        val accessToken: String = JSONObject(logInResponse.body).get("accessToken") as String
        val refreshToken: String = JSONObject(logInResponse.body).get("refreshToken") as String

        val refreshResponse: ResponseEntity<String> = requestNewAccessToken(refreshToken)
        assert(refreshResponse.statusCodeValue == 200)

        val newAccessToken: String = JSONObject(refreshResponse.body).get("accessToken") as String
        val newRefreshToken: String = JSONObject(refreshResponse.body).get("refreshToken") as String
        assert(accessToken != newAccessToken)
        assert(refreshToken != newRefreshToken)
        assert(decodeAndVerifyToken(refreshToken).expiresAt == decodeAndVerifyToken(newRefreshToken).expiresAt)
    }

    @Test
    fun `use access token to renew access token returns 400`() {
        val logInResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        val accessToken: String = JSONObject(logInResponse.body).get("accessToken") as String

        val refreshResponse: ResponseEntity<String> = requestNewAccessToken(accessToken)
        assert(refreshResponse.statusCodeValue == 400)
    }

    @Test
    fun `use refresh token and login with new access token`() {
        val logInResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        val refreshToken: String = JSONObject(logInResponse.body).get("refreshToken") as String

        val refreshResponse: ResponseEntity<String> = requestNewAccessToken(refreshToken)
        assert(refreshResponse.statusCodeValue == 200)
        val newAccessToken: String = JSONObject(refreshResponse.body).get("accessToken") as String

        val authHeaders = createAuthHeaders(newAccessToken)
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)
        assert(response.statusCodeValue == 200)
        logoutHeader(authHeaders)
    }

    @Test
    fun `using same refresh token twice returns 400`() {
        val logInResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        val refreshToken: String = JSONObject(logInResponse.body).get("refreshToken") as String

        val firstRefreshResponse: ResponseEntity<String> = requestNewAccessToken(refreshToken)
        assert(firstRefreshResponse.statusCodeValue == 200)
        val secondRefreshResponse: ResponseEntity<String> = requestNewAccessToken(refreshToken)
        assert(secondRefreshResponse.statusCodeValue == 400)
    }

    @Test
    fun `using same refresh token twice invalidates refresh token family`() {
        val logInResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        val refreshToken: String = JSONObject(logInResponse.body).get("refreshToken") as String

        val firstRefreshResponse: ResponseEntity<String> = requestNewAccessToken(refreshToken)
        assert(firstRefreshResponse.statusCodeValue == 200)

        val secondRefreshResponse: ResponseEntity<String> = requestNewAccessToken(refreshToken)
        assert(secondRefreshResponse.statusCodeValue == 400)

        val firstRefreshToken: String = JSONObject(firstRefreshResponse.body).get("refreshToken") as String
        val thirdRefreshResponse: ResponseEntity<String> = requestNewAccessToken(firstRefreshToken)
        assert(thirdRefreshResponse.statusCodeValue == 400)
    }

    // Login first to test GET with protected endpoint
    @Test
    fun `CORS using not allowed origin gives error`() {
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        authHeaders.add(HttpHeaders.ORIGIN, "http://notallowed.com")
        val request = HttpEntity("", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)
        assert(response.statusCodeValue == 403)
        assert(response.body == "Invalid CORS request")
    }

    // Login first to test GET with protected endpoint
    @Test
    fun `CORS using allowed origin works`() {
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        authHeaders.add(HttpHeaders.ORIGIN, ConfigUtil.allowedCorsOrigins[0])
        val request = HttpEntity("", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("/students"), HttpMethod.GET, request, String::class.java)
        assert(response.statusCodeValue == 200)
    }
}
