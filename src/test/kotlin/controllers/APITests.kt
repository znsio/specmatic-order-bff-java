package controllers

import `in`.specmatic.stub.ContractStub
import `in`.specmatic.stub.createStub
import com.intuit.karate.junit5.Karate
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
        private var service: ConfigurableApplicationContext? = null

        private lateinit var stub: ContractStub

        @BeforeAll
        @JvmStatic
        fun setUp() {
            stub = createStub()
            service = SpringApplication.run(Application::class.java)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            service?.stop()
            stub.close()
        }
    }
}