package com.component.orders.services

import com.component.orders.models.API
import com.component.orders.models.CreationStatus
import com.component.orders.models.ProductDetails
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI


@Service
class ProductManagementService : ProductService {

    @Value("\${order.api}")
    lateinit var orderAPIUrl: String

    override fun createOrder(orderJSON: JSONObject): CreationStatus {
        orderJSON.put("status", "pending")
        return CreationStatus(id = callCreateOrderAPI(orderJSON.toString()), status = "success")
    }

    override fun findProducts(type: String): List<ProductDetails> {
        val result = getFromAPI("/products?type=$type")
        val products = JSONArray(result)

        val availableProducts = 0.until(products.length()).map {
            ProductDetails(products.getJSONObject(it))
        }

        return availableProducts
    }

    private fun callCreateOrderAPI(order: String): Int {
        return writeJSONToAPI(API.CREATE_ORDER, order)?.let {
            JSONObject(it).getInt("id")
        } ?: error("No order id received in response to create request.")
    }

    private fun writeJSONToAPI(api: API, body: String): String? {
        val uri = URI.create("$orderAPIUrl${api.url}")
        val headers = HttpHeaders()
        headers["Content-Type"] = "application/json"
        val request = RequestEntity(body, headers, api.method, uri)
        val response = RestTemplate().exchange(request, String::class.java)
        return response.body
    }

    private fun getFromAPI(url: String): String =
        RestTemplate().getForEntity(URI.create("$orderAPIUrl$url"), String::class.java).body?.trim() ?: ""
}