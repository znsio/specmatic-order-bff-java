package com.component.orders.backend

import com.component.orders.models.*
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class OrderService {

    private val AUTHENTICATE_TOKEN = "API-TOKEN-HARI"

    @Value("\${order.api}")
    lateinit var orderAPIUrl: String

    fun createOrder(orderRequest: OrderRequest): Int {
        val apiUrl = orderAPIUrl + "/" + API.CREATE_ORDER.url
        val order = Order(orderRequest.productid, orderRequest.count, "pending")
        val headers = getHeaders()
        val requestEntity = HttpEntity(order, headers)
        val response = RestTemplate().exchange(
            apiUrl,
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
        if(response.body == null) {
            error("No order id received in Order API response.")
        }
        return JSONObject(response.body).getInt("id")
    }

    private fun getHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("Authenticate", AUTHENTICATE_TOKEN)
        return headers
    }

    fun findProducts(type: String): List<Product> {
        val apiUrl = orderAPIUrl + "/" + API.FIND_PRODUCTS.url + "?type=$type"
        val response = RestTemplate().getForEntity(apiUrl, List::class.java)
        return response.body.map {
            val product = it as Map<*, *>
            Product(
                product["id"].toString().toInt(),
                product["name"].toString(),
                product["inventory"].toString().toInt(),
                product["type"].toString()
            )
        }
    }
}