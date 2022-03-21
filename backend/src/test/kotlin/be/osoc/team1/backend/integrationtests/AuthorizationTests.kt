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
    var randomServerPort = 0

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

    // Log in with given email and password via post request to /login
    fun loginUser(email: String, password: String): ResponseEntity<String> {
        val input = "email=$email&password=$password"
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginRequest = HttpEntity(input, loginHeaders)

        return restTemplate.exchange(URI("$baseUrl/login"), HttpMethod.POST, loginRequest, String::class.java)
    }

    // Create the correct header for an access token to perform an authenticated request
    fun createAuthHeaders(accessToken: String): HttpHeaders {
        val authHeaders = HttpHeaders()
        authHeaders.add("Authorization", "Basic $accessToken")
        return authHeaders
    }

    // Token will stay valid through multiple tests unless you logout
    fun logoutToken(response: ResponseEntity<String>) {
        val accessToken: String = JSONObject(response.body).get("accessToken") as String
        val request = HttpEntity(null, createAuthHeaders(accessToken))
        restTemplate.exchange(URI("$baseUrl/logout"), HttpMethod.POST, request, String::class.java)
    }

    @Test
    fun `login with no credentials returns 401`() {
        val input = ""
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginRequest = HttpEntity(input, loginHeaders)

        val loginResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/login"), HttpMethod.POST, loginRequest, String::class.java)
        assert(loginResponse.statusCodeValue == 401)
    }

    @Test
    fun `login works as admin`() {
        val loginResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)

        assert(loginResponse.statusCodeValue == 200)
        assert(JSONObject(loginResponse.body).has("accessToken"))
        // assert(JSONObject(loginResponse.body).has("refreshToken"))

        logoutToken(loginResponse)
    }

    @Test
    fun `login works as coach`() {
        val loginResponse: ResponseEntity<String> = loginUser(coachEmail, coachPassword)

        assert(loginResponse.statusCodeValue == 200)
        assert(JSONObject(loginResponse.body).has("accessToken"))
        // assert(JSONObject(loginResponse.body).has("refreshToken"))

        logoutToken(loginResponse)
    }

    // This should probably be changed to check for whatever is supposed to happen
    @Test
    fun `login works as disabled`() {
        val loginResponse: ResponseEntity<String> = loginUser(disabledEmail, disabledPassword)

        assert(loginResponse.statusCodeValue == 200)
        assert(JSONObject(loginResponse.body).has("accessToken"))
        // assert(JSONObject(loginResponse.body).has("refreshToken"))

        logoutToken(loginResponse)
    }

    @Test
    fun `access token can be used after login`() {
        val loginResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        val accessToken: String = JSONObject(loginResponse.body).get("accessToken") as String

        val getRequest = HttpEntity(null, createAuthHeaders(accessToken))

        val getResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, getRequest, String::class.java)

        assert(getResponse.statusCodeValue == 200)

        logoutToken(loginResponse)
    }

    @Test
    fun `GET students returns 403 when not logged in`() {
        val response: ResponseEntity<String> = restTemplate.getForEntity<String>("$baseUrl/students")
        assert(response.statusCodeValue == 403)
    }

    @Test
    fun `GET students works when logged in as admin`() {
        val loginResponse: ResponseEntity<String> = loginUser(adminEmail, adminPassword)
        assert(loginResponse.statusCodeValue == 200)

        val accessToken: String = JSONObject(loginResponse.body).get("accessToken") as String
        val getRequest = HttpEntity(null, createAuthHeaders(accessToken))

        val getResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, getRequest, String::class.java)

        assert(getResponse.statusCodeValue == 200)
        assert(JSONArray(getResponse.body).getJSONObject(0).get("firstName") == testStudent.firstName)
        assert(JSONArray(getResponse.body).getJSONObject(0).get("lastName") == testStudent.lastName)

        logoutToken(loginResponse)
    }

    @Test
    fun `GET students works when logged in as coach`() {
        val loginResponse: ResponseEntity<String> = loginUser(coachEmail, coachPassword)
        assert(loginResponse.statusCodeValue == 200)

        val accessToken: String = JSONObject(loginResponse.body).get("accessToken") as String
        val getRequest = HttpEntity(null, createAuthHeaders(accessToken))

        val getResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, getRequest, String::class.java)

        assert(getResponse.statusCodeValue == 200)
        assert(JSONArray(getResponse.body).getJSONObject(0).get("firstName") == testStudent.firstName)
        assert(JSONArray(getResponse.body).getJSONObject(0).get("lastName") == testStudent.lastName)

        logoutToken(loginResponse)
    }

    @Test
    fun `GET students returns 403 when logged in as disabled`() {
        val loginResponse: ResponseEntity<String> = loginUser(disabledEmail, disabledPassword)
        assert(loginResponse.statusCodeValue == 200)

        val accessToken: String = JSONObject(loginResponse.body).get("accessToken") as String
        val getRequest = HttpEntity(null, createAuthHeaders(accessToken))

        val getResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, getRequest, String::class.java)

        assert(getResponse.statusCodeValue == 403)

        logoutToken(loginResponse)
    }

    @Test
    fun `Authentication with expired access token returns 401`() {
        // Test using an actual admin access token
        val accessToken: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9DT0FDSCIsIlJPTEVfRElTQUJMRUQiXSwiZXhwIjoxNjQ3ODE2OTEwfQ.26FNKdqV9OCotTMQRj2fB_HPuKti_YMKFlyTtRbkvDY"
        val getRequest = HttpEntity(null, createAuthHeaders(accessToken))

        val getResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, getRequest, String::class.java)
        assert(getResponse.statusCodeValue == 401)
    }

    @Test
    fun `Authentication with invalid access token returns 401`() {
        val accessToken: String = "in.val.id"
        val getRequest = HttpEntity(null, createAuthHeaders(accessToken))

        val getResponse: ResponseEntity<String> = restTemplate.exchange(URI("$baseUrl/students"), HttpMethod.GET, getRequest, String::class.java)
        assert(getResponse.statusCodeValue == 401)
    }

    // Extra test to check if you can use refresh token to renew access token

    // Extra test to check if refresh token gets cycled

    // These are the old tests, we will need to discuss how far we want to go with security testing
    // @Test
    // @WithMockUser(roles = ["USER"])
    // fun `GET succeeds when logged in as user`() {
    //     every { studentService.getAllStudents() } returns emptyList()
    //     mockMvc.perform(get("/students"))
    //         .andExpect(status().isOk)
    // }
    //
    // @Test
    // fun `POST returns 403 when not logged in`() {
    //     val status = StatusEnum.Yes
    //     every { studentService.setStudentStatus(testId, status) } just Runs
    //     mockMvc.perform(
    //         post("/students/$testId/status")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(status))
    //     ).andExpect(status().isForbidden)
    // }
    //
    // @Test
    // @WithMockUser(roles = ["USER"])
    // fun `POST returns 403 when logged in as user`() {
    //     val status = StatusEnum.Yes
    //     every { studentService.setStudentStatus(testId, status) } just Runs
    //     mockMvc.perform(
    //         post("/students/$testId/status")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(status))
    //     ).andExpect(status().isForbidden)
    // }
    //
    // @Test
    // @WithMockUser(roles = ["ADMIN"])
    // fun `POST succeeds when logged in as admin`() {
    //     val databaseId = UUID.randomUUID()
    //     every { studentService.addStudent(any()) } returns databaseId
    //     val mvcResult = mockMvc.perform(
    //         post("/students")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(jsonRepresentation)
    //     ).andExpect(status().isCreated).andReturn()
    //     val locationHeader = mvcResult.response.getHeader("Location")
    //     assert(locationHeader!!.endsWith("/students/$databaseId"))
    // }
}