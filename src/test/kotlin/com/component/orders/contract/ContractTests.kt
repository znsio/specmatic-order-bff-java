package com.component.orders.contract

import com.component.orders.Application
import `in`.specmatic.kafka.mock.KafkaMock
import `in`.specmatic.kafka.mock.model.Expectation
import `in`.specmatic.stub.ContractStub
import `in`.specmatic.stub.createStub
import `in`.specmatic.test.SpecmaticJUnitSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

class ContractTests : SpecmaticJUnitSupport() {

    companion object {
        private lateinit var context: ConfigurableApplicationContext
        private lateinit var httpStub: ContractStub
        private lateinit var kafkaMock: KafkaMock
        private const val APPLICATION_HOST = "localhost"
        private const val APPLICATION_PORT = "8080"
        private const val HTTP_STUB_HOST = "localhost"
        private const val HTTP_STUB_PORT = 8090
        private const val KAFKA_MOCK_HOST = "localhost"
        private const val KAFKA_MOCK_PORT = 9092
        private const val ACTUATOR_MAPPINGS_ENDPOINT =
            "http://$APPLICATION_HOST:$APPLICATION_PORT/actuator/mappings"
        private const val EXPECTED_NUMBER_OF_MESSAGES = 6

        @JvmStatic
        @BeforeAll
        fun setUp() {
            System.setProperty("host", APPLICATION_HOST)
            System.setProperty("port", APPLICATION_PORT)
            System.setProperty("endpointsAPI", ACTUATOR_MAPPINGS_ENDPOINT)
            System.setProperty("SPECMATIC_GENERATIVE_TESTS", "true")

            // Start Specmatic Http Stub and set the expectations
            httpStub = createStub(listOf("./src/test/resources"), HTTP_STUB_HOST, HTTP_STUB_PORT)

            // Start Specmatic Kafka Mock and set the expectations
            kafkaMock = KafkaMock.startInMemoryBroker(KAFKA_MOCK_HOST, KAFKA_MOCK_PORT)
            kafkaMock.setExpectations(listOf(Expectation("product-queries", EXPECTED_NUMBER_OF_MESSAGES)))

            // Start Springboot application
            val springApp = SpringApplication(Application::class.java)
            context = springApp.run()
        }

        @JvmStatic
        @AfterAll
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
    }
}

