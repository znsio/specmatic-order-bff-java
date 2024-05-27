package com.component.orders.backend

import com.component.orders.models.*
import com.component.orders.models.messages.ProductMessage
import com.google.gson.Gson
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*


@Service
class OrderService(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val authToken = "API-TOKEN-SPEC"
    // TODO - use jackson instead
    private val gson = Gson()

    @Value("\${order.api}")
    lateinit var orderAPIUrl: String

    @Value("\${kafka.topic}")
    lateinit var kafkaTopic: String

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
        if (response.body == null) {
            error("No order id received in Order API response.")
        }
        return JSONObject(response.body).getInt("id")
    }

    fun findProducts(type: String): List<Product> {
        println("KafkaTemplate --> $kafkaTemplate")
        val products = fetchProductsFromBackendAPI(type)
        products.forEach {
            val productMessage = ProductMessage(it.id, it.name, it.inventory)
            try {
                kafkaTemplate.send(kafkaTopic, gson.toJson(productMessage))
            } catch(e : Exception) {
                println("The exception --> $e")
                e.printStackTrace()
            }
        }
        return products
    }

    fun createProduct(newProduct: NewProduct): Int {
        val apiUrl = orderAPIUrl + "/" + API.CREATE_PRODUCTS.url
        val headers = getHeaders()
        val requestEntity = HttpEntity(newProduct, headers)
        val response = RestTemplate().exchange(
            apiUrl,
            API.CREATE_PRODUCTS.method,
            requestEntity,
            String::class.java
        )
        if (response.body == null) {
            error("No product id received in Product API response.")
        }
        return JSONObject(response.body).getInt("id")
    }

    private fun getHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("Authenticate", authToken)
        return headers
    }

    private fun fetchProductsFromBackendAPI(type: String): List<Product> {
        val apiUrl = orderAPIUrl + "/" + API.LIST_PRODUCTS.url + "?type=$type"
        val restTemplate = RestTemplate()
        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setConnectTimeout(3000)
        requestFactory.setReadTimeout(3000)
        restTemplate.setRequestFactory(requestFactory)
        val response = restTemplate.getForEntity(apiUrl, List::class.java)
        return response.body.map {
            val product = it as Map<*, *>
            Product(
                product["name"].toString(),
                product["type"].toString(),
                product["inventory"].toString().toInt(),
                product["id"].toString().toInt(),
            )
        }
    }
}