package com.component.orders.api

import com.component.orders.Application
import com.intuit.karate.junit5.Karate
import `in`.specmatic.kafka.mock.KafkaMock
import `in`.specmatic.kafka.mock.model.Expectation
import `in`.specmatic.stub.ContractStub
import `in`.specmatic.stub.createStub
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

class APITests {
    @Karate.Test
    fun apiTests(): Karate {
        return Karate().path("apiTests.feature").relativeTo(this::class.java)
    }
    companion object {
        private lateinit var context: ConfigurableApplicationContext
        private lateinit var httpStub: ContractStub
        private lateinit var kafkaMock: KafkaMock
        private const val HTTP_STUB_HOST = "localhost"
        private const val HTTP_STUB_PORT = 9000
        private const val KAFKA_MOCK_PORT = 9092
        private const val EXPECTED_NUMBER_OF_MESSAGES = 3

        @BeforeAll
        @JvmStatic
        fun setUp() {
            // Start Specmatic Http Stub
            httpStub = createStub(HTTP_STUB_HOST, HTTP_STUB_PORT)

            // Start Specmatic Kafka Mock and set the expectations
            kafkaMock = KafkaMock.create(KAFKA_MOCK_PORT)
            kafkaMock.start()
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

            // Verify Specmatic Kafka mock and shutdown
            kafkaMock.awaitMessages(EXPECTED_NUMBER_OF_MESSAGES)
            val result = kafkaMock.verifyExpectations()
            assertThat(result.success).isTrue
            assertThat(result.errors).isEmpty()
            kafkaMock.close()
            // Wait for Kafka server to stop
            Thread.sleep(15000)
        }
    }
}