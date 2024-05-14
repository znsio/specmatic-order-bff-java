package com.component.orders.contract

import com.component.orders.Application
import `in`.specmatic.core.Result
import `in`.specmatic.graphql.GraphQLSpecification
import `in`.specmatic.kafka.mock.KafkaMock
import `in`.specmatic.kafka.mock.model.Expectation
import `in`.specmatic.stub.ContractStub
import `in`.specmatic.stub.createStub
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import java.io.File
import java.util.stream.Stream
import kotlin.streams.asStream

class GraphQLContractTests {

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
        private const val EXPECTED_NUMBER_OF_MESSAGES = 1

        @JvmStatic
        @BeforeAll
        fun setUp() {
            System.setProperty("host", APPLICATION_HOST)
            System.setProperty("port", APPLICATION_PORT)
//            System.setProperty("endpointsAPI", ACTUATOR_MAPPINGS_ENDPOINT)
//            System.setProperty("SPECMATIC_GENERATIVE_TESTS", "true")

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
//            assertThat(result.success).withFailMessage(result.errors.joinToString()).isTrue
            // Wait for Kafka server to stop
            Thread.sleep(5000)
        }
    }

    @TestFactory
    fun graphQLContractTests(): Stream<DynamicTest> {
        val spec = GraphQLSpecification.fromSpec(File("./src/main/resources/graphql/products.graphqls").readText())

        val feature = spec.toFeature()

        val contractTests = feature.generateContractTests(emptyList())

        return contractTests.map { test ->
            DynamicTest.dynamicTest(test.testDescription()) {
                val result = test.runTest("http://${APPLICATION_HOST}:${APPLICATION_PORT}" ,30000)

                assertThat(result.first).withFailMessage(result.first.reportString()).isInstanceOf(Result.Success::class.java)

            }
        }.asStream()
    }

}
