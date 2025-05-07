package com.component.orders

import io.specmatic.stub.ContractStub
import io.specmatic.stub.createStub
import io.specmatic.test.SpecmaticContractTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ContractTests : SpecmaticContractTest {

    companion object {
        private lateinit var httpStub: ContractStub
        private const val APPLICATION_HOST = "localhost"
        private const val APPLICATION_PORT = "8080"
        private const val HTTP_STUB_HOST = "localhost"
        private const val HTTP_STUB_PORT = 8090
        private const val ACTUATOR_MAPPINGS_ENDPOINT = "http://$APPLICATION_HOST:$APPLICATION_PORT/actuator/mappings"
        private const val EXCLUDED_ENDPOINTS = "'/health'"
        @JvmStatic
        @BeforeAll
        fun setUp() {
            System.setProperty("host", APPLICATION_HOST)
            System.setProperty("port", APPLICATION_PORT)
            System.setProperty("endpointsAPI", ACTUATOR_MAPPINGS_ENDPOINT)
            System.setProperty("filter","PATH!=$EXCLUDED_ENDPOINTS")
            // Start Specmatic Http Stub and set the expectations
            httpStub = createStub(listOf("./src/test/resources/domain_service"), HTTP_STUB_HOST, HTTP_STUB_PORT)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            // Shutdown Specmatic Http Stub
            httpStub.close()
        }
    }
}
