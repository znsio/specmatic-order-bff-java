package com.component.orders.contract

import io.specmatic.async.specmatic.kafka.VersionInfo
import io.specmatic.kafka.mock.KafkaMock
import io.specmatic.kafka.mock.model.Expectation
import io.specmatic.stub.ContractStub
import io.specmatic.stub.createStub
import io.specmatic.test.SpecmaticContractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ContractTests : SpecmaticContractTest {

    companion object {
        private lateinit var httpStub: ContractStub
        private lateinit var kafkaMock: KafkaMock
        private const val APPLICATION_HOST = "localhost"
        private const val APPLICATION_PORT = "8080"
        private const val HTTP_STUB_HOST = "localhost"
        private const val HTTP_STUB_PORT = 8090
        private const val KAFKA_MOCK_HOST = "localhost"
        private const val KAFKA_MOCK_PORT = 9092
        private const val EXPECTED_NUMBER_OF_MESSAGES = 4
        private const val EXCLUDED_ENDPOINTS = "'/health'"
        @JvmStatic
        @BeforeAll
        fun setUp() {
            println("Using specmatic kafka - ${VersionInfo.describe()}")
            System.setProperty("host", APPLICATION_HOST)
            System.setProperty("port", APPLICATION_PORT)
            System.setProperty("filter","PATH!=$EXCLUDED_ENDPOINTS")
            // Start Specmatic Http Stub and set the expectations
            httpStub = createStub(listOf("./src/test/resources/domain_service"), HTTP_STUB_HOST, HTTP_STUB_PORT)

            // Start Specmatic Kafka Mock and set the expectations
            kafkaMock = KafkaMock.startInMemoryBroker(KAFKA_MOCK_HOST, KAFKA_MOCK_PORT)
            kafkaMock.setExpectations(listOf(Expectation("product-queries", EXPECTED_NUMBER_OF_MESSAGES)))
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            // Shutdown Specmatic Http Stub
            httpStub.close()

            val result = kafkaMock.stop()
            assertThat(result.success).withFailMessage(result.errors.joinToString()).isTrue
        }
    }
}
