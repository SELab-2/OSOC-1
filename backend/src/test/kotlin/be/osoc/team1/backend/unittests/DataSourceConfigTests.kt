package be.osoc.team1.backend.unittests

import be.osoc.team1.backend.DataSourceConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.core.env.Environment

class DataSourceConfigTests {

    private fun getEnvironment(url: String? = null, username: String? = null, password: String? = null): Environment {
        val environment: Environment = mockk()
        every { environment.getProperty("OSOC_DB_URL") } returns url
        every { environment.getProperty("OSOC_DB_USERNAME") } returns username
        every { environment.getProperty("OSOC_DB_PASSWORD") } returns password
        return environment
    }

    @Test
    fun `DataSourceConfig loads properties from the properties file when the environment variables are not set`() {
        val dataSourceConfig = DataSourceConfig(getEnvironment())
        dataSourceConfig.getDataSource()
    }
}
