package com.component.orders.api

import com.component.orders.Application
import com.intuit.karate.junit5.Karate
import `in`.specmatic.kafka.mock.KafkaMock
import `in`.specmatic.stub.ContractStub
import `in`.specmatic.stub.createStub
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

@Disabled
class APITests {
    @Karate.Test
    fun apiTests(): Karate {
        return Karate().path("apiTests.feature").relativeTo(this::class.java)
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
            service = SpringApplication.run(Application::class.java)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            service?.close()
            stub.close()
            kafkaMock.stop()
            // Wait for Kafka server to stop
            Thread.sleep(10000)
        }
    }
}