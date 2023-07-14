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
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import java.io.File

class ContractTests: SpecmaticJUnitSupport() {

    companion object {
        private var context: ConfigurableApplicationContext? = null
        private lateinit var stub: ContractStub
        private lateinit var kafkaMock: KafkaMock
        private const val SPECMATIC_TEST_HOST = "localhost"
        private const val SPECMATIC_TEST_PORT = "8080"
        private const val SPECMATIC_STUB_HOST = "localhost"
        private const val SPECMATIC_STUB_PORT = 9000
        private const val ACTUTATOR_MAPPINGS_ENDPOINT = "http://$SPECMATIC_TEST_HOST:$SPECMATIC_TEST_PORT/actuator/mappings"

        @JvmStatic
        @BeforeAll
        fun setUp() {
            System.setProperty("host", SPECMATIC_TEST_HOST)
            System.setProperty("port", SPECMATIC_TEST_PORT)
            System.setProperty("endpointsAPI", ACTUTATOR_MAPPINGS_ENDPOINT)

            stub = createStub(SPECMATIC_STUB_HOST, SPECMATIC_STUB_PORT)

            val expectationJsonString = File("./src/test/resources/expectation.json").readText()
            stub.setExpectation(expectationJsonString)

            kafkaMock = KafkaMock.create()
            kafkaMock.start()
            kafkaMock.setExpectations(listOf(Expectation("product-queries", 3)))

            val springApp = SpringApplication(Application::class.java)
            context = springApp.run()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            context!!.close()
            stub.close()
            kafkaMock.close()
            // Wait for Kafka server to stop
            Thread.sleep(15000)
        }
    }

    @Test
    fun `test expectations set on the kafka mock are met`() {
        kafkaMock.awaitMessages(3)
        val result = kafkaMock.verifyExpectations()
        assertThat(result.success).isTrue
        assertThat(result.errors).isEmpty()
    }
}

