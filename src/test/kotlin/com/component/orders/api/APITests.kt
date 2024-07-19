package com.component.orders.api

import com.component.orders.Application
import com.intuit.karate.junit5.Karate
import io.specmatic.kafka.mock.KafkaMock
import io.specmatic.kafka.mock.model.Expectation
import io.specmatic.stub.ContractStub
import io.specmatic.stub.createStub
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

class APITests {

    @Karate.Test
    fun apiTests(): Karate {
        return Karate().path(KARATE_FEATURE_FILE).relativeTo(this::class.java)
    }

    companion object {
        private lateinit var context: ConfigurableApplicationContext
        private lateinit var httpStub: ContractStub
        private lateinit var kafkaMock: KafkaMock
        private const val HTTP_STUB_HOST = "localhost"
        private const val HTTP_STUB_PORT = 8090
        private const val KAFKA_MOCK_HOST = "localhost"
        private const val KAFKA_MOCK_PORT = 9092
        private const val EXPECTED_NUMBER_OF_MESSAGES = 3

        @BeforeAll
        @JvmStatic
        fun setUp() {
            // Start Specmatic Http Stub
            httpStub = createStub(HTTP_STUB_HOST, HTTP_STUB_PORT, strict = true)

            // Start Specmatic Kafka Mock and set the expectations
            kafkaMock = KafkaMock.startInMemoryBroker(KAFKA_MOCK_HOST, KAFKA_MOCK_PORT)
            kafkaMock.setExpectations(listOf(Expectation("product-queries", EXPECTED_NUMBER_OF_MESSAGES)))

            // Start Springboot application
            context = SpringApplication.run(Application::class.java)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            // Shutdown Springboot application
            context.close()

            // Shutdown Specmatic Http Stub
            httpStub.close()

            val result = kafkaMock.stop()
            assertThat(result.success).withFailMessage(result.errors.joinToString()).isTrue
            // Wait for Kafka server to stop
            Thread.sleep(5000)
        }

        private const val KARATE_FEATURE_FILE = "apiTests.feature"
    }
}