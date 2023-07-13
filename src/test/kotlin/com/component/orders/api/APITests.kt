package com.component.orders.api

import com.component.orders.Application
import com.intuit.karate.junit5.Karate
import `in`.specmatic.kafka.mock.KafkaMock
import `in`.specmatic.kafka.mock.model.Expectation
import `in`.specmatic.stub.ContractStub
import `in`.specmatic.stub.createStub
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

class APITests {
    @Karate.Test
    fun apiTests(): Karate {
        return Karate().path("apiTests.feature").relativeTo(this::class.java)
    }

    @Test
    fun `test expectations set on the kafka mock are met`() {
        kafkaMock.awaitMessages(3)
        val result = kafkaMock.verifyExpectations()
        Assertions.assertThat(result.success).isTrue
        Assertions.assertThat(result.errors).isEmpty()
    }

    companion object {
        private var service: ConfigurableApplicationContext? = null
        private lateinit var stub: ContractStub
        private lateinit var kafkaMock: KafkaMock

        @BeforeAll
        @JvmStatic
        fun setUp() {
            stub = createStub()
            kafkaMock = KafkaMock.create()
            kafkaMock.start()
            kafkaMock.setExpectations(listOf(Expectation("product-queries", 3)))
            service = SpringApplication.run(Application::class.java)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            service?.close()
            stub.close()
            kafkaMock.stop()
            // Wait for Kafka server to stop
            Thread.sleep(15000)
        }
    }
}