# Specmatic Sample Client Application

![HTML client talks to client API which talks to backend api](specmatic-sample-architecture.svg)

BFF = Backend For Frontend, the API invoked by the HTTP calls in the client HTML page (Website UI)

This project contains a small website UI, and an accompanying BFF (backend for frontend).

The BFF talks to the [backend API](https://github.com/znsio/specmatic-order-api).

Here is the [contract](https://github.com/znsio/specmatic-order-contracts/blob/main/in/specmatic/examples/store/api_order_v1.yaml) governing the interaction of the BFF with the backend API.
 
The architecture diagram was created using the amazing free online SVG editor at [Vectr](https://vectr.com).
