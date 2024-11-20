# Specmatic Sample: Springboot BFF calling Domain API

* [Specmatic Website](https://specmatic.io)
* [Specmatic Documentation](https://specmatic.io/documentation.html)

This sample project demonstrates how we can practice contract-driven development and contract testing in a SpringBoot (Kotlin) application that depends on an external domain service and Kafka. Here, Specmatic is used to stub calls to domain API service based on its OpenAPI spec and mock Kafka based on its AsyncAPI spec.

Here is the domain api [contract/open api spec](https://github.com/znsio/specmatic-order-contracts/blob/main/io/specmatic/examples/store/openapi/api_order_v3.yaml)

Here is the [AsyncAPI spec](https://github.com/znsio/specmatic-order-contracts/blob/main/io/specmatic/examples/store/asyncapi/kafka.yaml) of Kafka that defines the topics and message schema.

## Definitions
* BFF: Backend for Front End
* Domain API: API managing the domain model
* Specmatic Stub/Mock Server: Create a server that can act as a real service using its OpenAPI or AsyncAPI spec

## Background
A typical web application might look like this. We can use Specmatic to practice contract-driven development and test all the components mentioned below. In this sample project, we look at how to do this for nodejs BFF which is dependent on Domain API Service and Kafka demonstrating both OpenAPI and AsyncAPI support in specmatic.

![HTML client talks to client API which talks to backend API](assets/specmatic-order-bff-architecture.gif)

## Tech
1. Spring boot
2. Specmatic
3. Specmatic Beta extension (for mocking Kafka)

## Run Tests

This will start the specmatic stub server for domain api and kafka mock using the information in specmatic.yaml and run contract tests using Specmatic.
1. Using gradle -
   ```shell
     ./gradlew test
   ```
2. Using docker -
   - Start Docker Desktop
   - Run the application `./gradlew bootRun`
   - Run the tests `docker run --network host -v "$PWD/specmatic.yaml:/usr/src/app/specmatic.yaml" -v "$PWD/build/reports/specmatic:/usr/src/app/build/reports/specmatic"  znsio/specmatic test --port=8080 --host=host.docker.internal`

# Break down each component to understand what is happening

### Prerequisites

1. Docker Desktop
2. Java and Gradle
3. If you are on a Windows OS, please use PowerShell.
 
### Start the dependent components

1. Start domain api stub server

```shell
docker run -v "$PWD/specmatic.yaml:/usr/src/app/specmatic.yaml" -p 8090:9000 znsio/specmatic stub
```

2. Start Kafka stub server

```shell
docker run -p 9092:9092 -p 2181:2181 -v "$PWD/specmatic.yaml:/usr/src/app/specmatic.yaml" znsio/specmatic-kafka-trial
```

## Start BFF Server
This will start the springboot BFF server
```shell
./gradlew bootRun
```

## Test if everything is working

Note: For Windows OS, add `.exe` extension to curl command on PowerShell or use `cmd.exe` instead.

```shell
curl -H "pageSize: 10" "http://localhost:8080/findAvailableProducts"
```

Your result should look like:
```json
[{"id":698,"name":"NUBYR","type":"book","inventory":278}]
```

Also observe the logs in the Specmatic HTTP Stub Server and Specmatic Kafka Mock Server.
