package be.osoc.team1.backend

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Properties
import javax.sql.DataSource

@Configuration
class DataSourceConfig {
    private lateinit var applicationProperties: Properties

    /**
     * A helper function that gets the value of the environment variable [name]. When the environment variable is not
     * set it will get the property [propertyName] from the application.properties file. The application.properties file
     * is lazily loaded and reused for subsequent requests.
     */
    private fun getProperty(name: String, propertyName: String): String {
        var value = System.getenv(name)
        if (value == null) {
            if (!this::applicationProperties.isInitialized) {
                applicationProperties = Properties()
                applicationProperties.load(javaClass.classLoader.getResourceAsStream("application.properties"))
            }
            value = applicationProperties.getProperty(propertyName)
        }
        return value
    }

    @Bean
    fun getDataSource(): DataSource {
        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.url(getProperty("OSOC_DB_URL", "spring.datasource.url"))
        dataSourceBuilder.username(getProperty("OSOC_DB_USERNAME", "spring.datasource.username"))
        dataSourceBuilder.password(getProperty("OSOC_DB_PASSWORD", "spring.datasource.password"))
        return dataSourceBuilder.build()
    }
}
