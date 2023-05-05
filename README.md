# Specmatic Sample: Springboot BFF calling Domain API

* [Specmatic Website](https://specmatic.in)
* [Specmatic Documenation](https://specmatic.in/documentation.html)

This sample project demonstrates how we can contract driven development and testing of a BFF by stubbing calls to domain api service using specmatic stub  option using the domain api's OpenAPI spec.

Here is the [contract/open api spec](https://github.com/znsio/specmatic-order-contracts/blob/main/in/specmatic/examples/store/api_order_v1.yaml) of the domain api

### Tech
1. Spring boot
2. Specmatic
3. Karate
 
### Start BFF Server
This will start the springboot based backend api server
```
./gradlew bootRun
```
### Run Tests
This will start the specmatic stub server for domain api using the information in specmatic.json and run the karate tests that expects the domain api at port 9000.
```
./gradlew test
```
