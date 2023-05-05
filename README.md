# Specmatic Sample: Springboot BFF calling Domain API

* [Specmatic Website](https://specmatic.in)
* [Specmatic Documenation](https://specmatic.in/documentation.html)

This sample project demonstrates how we can contract driven development and testing of a BFF by stubbing calls to domain api service using specmatic stub  option and domain api OpenAPI spec.

Here is the domain api [contract/open api spec](https://github.com/znsio/specmatic-order-contracts/blob/main/in/specmatic/examples/store/api_order_v1.yaml)

## Definitions
* BFF: Backend for Front End
* Domain API: API managing the domain model
* Specmatic Stub Server: Create a server that can replace a real service using its open api spec

## Background
A typical web application might look like this. Specmatic can contract driven development and testing of all the three components below. In this sample project, we look how to do this for BFF which is dependent on Domain API.

![HTML client talks to client API which talks to backend api](specmatic-sample-architecture.svg)
 
_The architecture diagram was created using the amazing free online SVG editor at [Vectr](https://vectr.com)._

## Tech
1. Spring boot
2. Specmatic
3. Karate
 
## Start BFF Server
This will start the springboot BFF server
```shell
./gradlew bootRun
```
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
