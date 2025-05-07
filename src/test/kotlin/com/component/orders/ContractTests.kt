package com.component.orders

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ContractTests {

    companion object {
        private const val APPLICATION_HOST = "host.docker.internal"
        private const val APPLICATION_PORT = 8080
        private const val HTTP_STUB_PORT = 8090
        private const val ACTUATOR_MAPPINGS_ENDPOINT = "http://$APPLICATION_HOST:$APPLICATION_PORT/actuator/mappings"
        private const val EXCLUDED_ENDPOINTS = "'/health'"

        @Container
        private val stubContainer: GenericContainer<*> = GenericContainer("znsio/specmatic-openapi")
            .withCommand(
                "stub",
                "--examples=examples",
                "--port=$HTTP_STUB_PORT",
            )
            .withCreateContainerCmdModifier({ cmd ->
                cmd.hostConfig?.withPortBindings(
                    PortBinding(Ports.Binding.bindPort(8090), ExposedPort(8090))
                )
            })
            .withExposedPorts(8090)
            .withFileSystemBind(
                "./src/test/resources/domain_service",
                "/usr/src/app/examples",
                BindMode.READ_ONLY
            )
            .withFileSystemBind(
                "./src/test/resources/specmatic.yaml",
                "/usr/src/app/specmatic.yaml",
                BindMode.READ_ONLY
            )
            .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200))
            .withLogConsumer { print(it.utf8String) }

        private val testContainer: GenericContainer<*> = GenericContainer("znsio/specmatic-openapi")
            .withCommand(
                "test",
                "--examples=examples",
                "--host=$APPLICATION_HOST",
                "--port=$APPLICATION_PORT",
                "--filter=PATH!=$EXCLUDED_ENDPOINTS"
            )
            .withEnv("endpointsAPI", ACTUATOR_MAPPINGS_ENDPOINT)
            .withNetworkMode("host")
            .withFileSystemBind(
                "./src/test/resources/bff",
                "/usr/src/app/examples",
                BindMode.READ_ONLY
            )
            .withFileSystemBind(
                "./src/test/resources/specmatic.yaml",
                "/usr/src/app/specmatic.yaml",
                BindMode.READ_ONLY
            )
            .withFileSystemBind(
                "./build/reports/specmatic",
                "/usr/src/app/build/reports/specmatic",
                BindMode.READ_WRITE
            )
            .waitingFor(Wait.forLogMessage(".*Tests run:.*", 1))
            .withLogConsumer { print(it.utf8String) }
    }

    @Test
    fun contractTestsShouldPass() {
        testContainer.start()
        val hasSucceeded = testContainer.logs.contains("Failures: 0")
        assertThat(hasSucceeded).isTrue()
    }
}
