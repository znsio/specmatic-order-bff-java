# Specmatic Sample: Springboot BFF calling Domain API

* [Specmatic Website](https://specmatic.in)
* [Specmatic Documenation](https://specmatic.in/documentation.html)

This sample project demonstrates how we can practice contract-driven development and contract testing in a SpringBoot (Kotlin) application that depends on an external domain service and Kafka. Here, Specmatic is used to stub calls to domain API service based on its OpenAPI spec and mock Kafka based on its AsyncAPI spec.

Here is the domain api [contract/open api spec](https://github.com/znsio/specmatic-order-contracts/blob/main/in/specmatic/examples/store/api_order_v1.yaml)

Here is the [AsyncAPI spec](https://github.com/znsio/specmatic-order-contracts/blob/main/in/specmatic/examples/store/API_order_v1.yaml) of Kafka that defines the topics and message schema.

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
4. Karate
 
## Start BFF Server
This will start the springboot BFF server
```shell
./gradlew bootRun
```
_*Note:* When running this command you might get an error:_
```shell
Could not find in. specmatic: specmatic-kafka:0.xx.yy.
```
You would have got this error because you don't have access to Specmatic Kafka. To fix this issue, reach out to us at specmatic@xnsio.com and we'll add you to our beta program and provide you access.

Access find orders api at http://localhost:8080/findAvailableProducts
_*Note:* Unless domain api service is running on port 9000, above requests will fail. Move to next section for solution!_

### Start BFF Server with Domain API Stub
1. Download Specmatic Jar from [github](https://github.com/znsio/specmatic/releases)

2. Start domain api stub server
```shell
java -jar specmatic.jar stub
```
Access find orders api again at http://localhost:8080/findAvailableProducts with result like
```json
[{"id":698,"name":"NUBYR","type":"book","inventory":278}]
```

## Run Tests
This will start the specmatic stub server for domain api using the information in specmatic.json and run the karate tests that expects the domain api at port 9000.
```shell
./gradlew test
```
