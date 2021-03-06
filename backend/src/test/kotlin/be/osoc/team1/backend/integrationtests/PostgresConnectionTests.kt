package be.osoc.team1.backend.integrationtests

import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.junit.jupiter.Testcontainers

// Basic test to ensure postgresql testcontainer works
@SpringBootTest
@Testcontainers
class PostgresConnectionTests {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun contextLoads() {
    }

    @Test
    fun `when database is connected then it should be a Postgres instance running`() {
        val actualDatabase = jdbcTemplate.queryForObject("SELECT version()", String::class.java)
        actualDatabase shouldContain "PostgreSQL"
    }
}
