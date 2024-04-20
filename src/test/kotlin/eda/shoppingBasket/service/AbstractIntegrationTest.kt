package eda.shoppingBasket.service

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.lifecycle.Startables
import org.testcontainers.utility.DockerImageName

@DirtiesContext
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class AbstractIntegrationTest {
    companion object {
        val db = MySQLContainer("mysql")
        val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
            .withEmbeddedZookeeper()

        @JvmStatic
        @BeforeAll
        fun startDBContainer() {
            Startables.deepStart(
                db,
                kafka
            ).join()
        }

        @JvmStatic
        @AfterAll
        fun stopDBContainer() {
            db.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDbContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerKafkaContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers)
        }
    }
}