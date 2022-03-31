package be.osoc.team1.backend.integrationtests

import be.osoc.team1.backend.entities.Role
import be.osoc.team1.backend.entities.Student
import be.osoc.team1.backend.entities.User
import be.osoc.team1.backend.repositories.StudentRepository
import be.osoc.team1.backend.repositories.UserRepository
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.ClassRule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthorizationTests(@Autowired val restTemplate: TestRestTemplate) {

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

    @Before
    fun setup() {
        baseUrl = "http://localhost:$randomServerPort"
    }

    companion object {
        @ClassRule
        @Container
        private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" } // VERY IMPORTANT!
        }
    }

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @LocalServerPort
    private var randomServerPort: Int = 0

    private var baseUrl: String = ""

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

        return restTemplate.exchange(URI("$baseUrl/login"), HttpMethod.POST, loginRequest, String::class.java)
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
        restTemplate.exchange(URI("$baseUrl/logout"), HttpMethod.POST, request, String::class.java)
    }

    /**
     * Create a header that can be used for further requests based upon given credentials
     */
    fun getAuthenticatedHeader(email: String, password: String): HttpHeaders {
        val response: ResponseEntity<String> = loginUser(email, password)
        val accessToken: String = JSONObject(response.body).get("accessToken") as String
        val authHeaders = HttpHeaders()
        authHeaders.add("Authorization", "Basic $accessToken")
        return authHeaders
    }

    @Test
    fun `login with no credentials returns 401`() {
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginRequest = HttpEntity("", loginHeaders)
        val loginResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/login"), HttpMethod.POST, loginRequest, String::class.java)
        assert(loginResponse.statusCodeValue == 401)
    }

    @Test
    fun `login with only email returns 401`() {
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginRequest = HttpEntity("email=admin@admin.com", loginHeaders)
        val loginResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/login"), HttpMethod.POST, loginRequest, String::class.java)
        assert(loginResponse.statusCodeValue == 401)
    }

    @Test
    fun `login with only password returns 401`() {
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginRequest = HttpEntity("password=adminPassword", loginHeaders)
        val loginResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/login"), HttpMethod.POST, loginRequest, String::class.java)
        assert(loginResponse.statusCodeValue == 401)
    }

    @Test
    fun `login works as admin`() {
        val loginResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        assert(loginResponse.statusCodeValue == 200)
        assert(JSONObject(loginResponse.body).has("accessToken"))
        logoutResponse(loginResponse)
    }

    @Test
    fun `login works as coach`() {
        val loginResponse: ResponseEntity<String> = loginUser(coachEmail, coachPassword)
        assert(loginResponse.statusCodeValue == 200)
        assert(JSONObject(loginResponse.body).has("accessToken"))
        logoutResponse(loginResponse)
    }

    @Test
    fun `login works as disabled`() {
        val loginResponse: ResponseEntity<String> = loginUser(disabledEmail, disabledPassword)
        assert(loginResponse.statusCodeValue == 200)
        assert(JSONObject(loginResponse.body).has("accessToken"))
        logoutResponse(loginResponse)
    }

    @Test
    fun `wrong authorization header returns 403`() {
        val loginResponse: ResponseEntity<String> = loginUser(disabledEmail, disabledPassword)
        val accessToken: String = JSONObject(loginResponse.body).get("accessToken") as String
        val authHeaders = HttpHeaders()
        authHeaders.add("Authorization", "Invalid $accessToken")
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, request, String::class.java)
        assert(response.statusCodeValue == 403)
        logoutResponse(loginResponse)
    }

    @Test
    fun `access token can be used after login`() {
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, request, String::class.java)

        assert(response.statusCodeValue == 200)
        logoutHeader(authHeaders)
    }

    @Test
    fun `GET students returns 403 when not logged in`() {
        val response: ResponseEntity<String> = restTemplate.getForEntity<String>("$baseUrl/students")
        assert(response.statusCodeValue == 403)
    }

    @Test
    fun `GET students works when logged in as admin`() {
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, request, String::class.java)

        assert(response.statusCodeValue == 200)
        assert(JSONArray(response.body).getJSONObject(0).get("firstName") == testStudent.firstName)
        assert(JSONArray(response.body).getJSONObject(0).get("lastName") == testStudent.lastName)
        logoutHeader(authHeaders)
    }

    @Test
    fun `GET students works when logged in as coach`() {
        val authHeaders = getAuthenticatedHeader(coachEmail, coachPassword)
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, request, String::class.java)

        assert(response.statusCodeValue == 200)
        assert(JSONArray(response.body).getJSONObject(0).get("firstName") == testStudent.firstName)
        assert(JSONArray(response.body).getJSONObject(0).get("lastName") == testStudent.lastName)
        logoutHeader(authHeaders)
    }

    @Test
    fun `GET students returns 403 when logged in as disabled`() {
        val authHeaders = getAuthenticatedHeader(disabledEmail, disabledPassword)
        val request = HttpEntity(null, authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, request, String::class.java)

        assert(response.statusCodeValue == 403)
        logoutHeader(authHeaders)
    }

    @Test
    fun `Authentication with invalid access token returns 401`() {
        val accessToken: String = "in.val.id"
        val request = HttpEntity(null, createAuthHeaders(accessToken))

        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, request, String::class.java)
        assert(response.statusCodeValue == 401)
    }

    @Test
    fun `changing role as admin returns 204`() {
        val userId = coachUser.id
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        authHeaders.add("Content-Type", "application/json")
        val request = HttpEntity("\"Disabled\"", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/users/$userId/role"), HttpMethod.POST, request, String::class.java)

        assert(response.statusCodeValue == 204)
        assert(userRepository.findByEmail(coachUser.email).get(0).role == Role.Disabled)
        logoutHeader(authHeaders)
    }

    @Test
    fun `changing role as coach returns 403`() {
        val userId = disabledUser.id
        val authHeaders = getAuthenticatedHeader(coachEmail, coachPassword)
        authHeaders.add("Content-Type", "application/json")
        val request = HttpEntity("\"Coach\"", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/users/$userId/role"), HttpMethod.POST, request, String::class.java)

        assert(response.statusCodeValue == 403)
        assert(userRepository.findByEmail(disabledUser.email).get(0).role == Role.Disabled)
        logoutHeader(authHeaders)
    }

    @Test
    fun `changing role as disabled returns 403`() {
        val userId = coachUser.id
        val authHeaders = getAuthenticatedHeader(disabledEmail, disabledPassword)
        authHeaders.add("Content-Type", "application/json")
        val request = HttpEntity("\"Disabled\"", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/users/$userId/role"), HttpMethod.POST, request, String::class.java)

        assert(response.statusCodeValue == 403)
        assert(userRepository.findByEmail(coachUser.email).get(0).role == Role.Coach)
        logoutHeader(authHeaders)
    }

    @Test
    fun `changing role as admin on last admin returns 403`() {
        val userId = adminUser.id
        val authHeaders = getAuthenticatedHeader(adminEmail, adminPassword)
        authHeaders.add("Content-Type", "application/json")
        val request = HttpEntity("\"Disabled\"", authHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/users/$userId/role"), HttpMethod.POST, request, String::class.java)

        assert(response.statusCodeValue == 403)
        assert(userRepository.findByEmail(adminUser.email).get(0).role == Role.Admin)
        logoutHeader(authHeaders)
    }

    @Test
    fun `encoding password should take around 1 second`() {
        val start = System.currentTimeMillis()
        BCryptPasswordEncoder(14).encode(adminPassword)
        val passedTimeMillis = System.currentTimeMillis() - start
        assert(passedTimeMillis > 700)
        assert(passedTimeMillis < 1300)
    }

    // Test to check if you can use refresh token to renew access token

    // Test to check if refresh token gets cycled
}
