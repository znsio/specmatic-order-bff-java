package com.component.orders.contract

import `in`.specmatic.graphql.stub.GraphQLStub
import `in`.specmatic.graphql.test.SpecmaticGraphQLContractTest
import `in`.specmatic.stub.ContractStub
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

class SelfLoopTest : SpecmaticGraphQLContractTest {
    companion object {
        private lateinit var graphQLStub: GraphQLStub
        private const val APPLICATION_HOST = "localhost"
        private const val APPLICATION_PORT = "9000"

        @JvmStatic
        @BeforeAll
        fun setUp() {
            System.setProperty("host", APPLICATION_HOST)
            System.setProperty("port", APPLICATION_PORT)

            graphQLStub = GraphQLStub.createGraphQLStub("localhost", 9000)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            graphQLStub.close()
        }
    }
}
