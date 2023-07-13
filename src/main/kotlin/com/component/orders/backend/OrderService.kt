package com.component.orders.backend

import com.component.orders.models.*
import com.component.orders.models.messages.ProductMessage
import com.google.gson.Gson
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class OrderService {
    private val AUTHENTICATE_TOKEN = "API-TOKEN-HARI"
    private val gson = Gson()

    @Value("\${order.api}")
    lateinit var orderAPIUrl: String

    fun createOrder(orderRequest: OrderRequest): Int {
        val apiUrl = orderAPIUrl + "/" + API.CREATE_ORDER.url
        val order = Order(orderRequest.productid, orderRequest.count, "pending")
        val headers = getHeaders()
        val requestEntity = HttpEntity(order, headers)
        val response = RestTemplate().exchange(
            apiUrl,
            API.CREATE_ORDER.method,
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
        val products = fetchProductsFromBackendAPI(type)
        products.forEach {
            publishProductOnKafkaTopic(gson.toJson(ProductMessage(it.id, it.name, it.inventory)))
        }
        return products
    }

    private fun fetchProductsFromBackendAPI(type: String): List<Product> {
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

    private fun publishProductOnKafkaTopic(productMessage: String) {
        val props = Properties()
        props["bootstrap.servers"] = "localhost:9092"
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        val producer = KafkaProducer<String, String>(props)
        producer.send(ProducerRecord("product-queries", productMessage))
    }
}