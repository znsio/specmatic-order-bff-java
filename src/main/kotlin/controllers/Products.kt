package controllers

import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.net.URI

data class PetInfo(val id: Int, val name: String)
data class CreationStatus(val id: Int, val status: String)

enum class API(val method: HttpMethod, val url: String) {
    CREATE_ORDER(HttpMethod.POST, "/orders")
}

data class ProductDetails(val id: Int, val name: String) {
    constructor(json: JSONObject): this(json.getInt("id"), json.getString("name"))
}

@RestController
class WebsiteAPI {
    @Value("\${order.api}")
    lateinit var orderAPIUrl: String

    @PostMapping("/orders", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createOrder(@RequestBody order: String): CreationStatus {
        val orderJSON = JSONObject(order)
        orderJSON.put("status", "pending")
        return CreationStatus(id = callCreateOrderAPI(orderJSON.toString()), status = "success")
    }

    @GetMapping("/findAvailableProducts", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAvailableProducts(
        @RequestParam(
            name = "type",
            required = true
        ) type: String
    ): ResponseEntity<List<ProductDetails>> {
        try {
            val result = getFromAPI("/products?type=$type")
            val products = JSONArray(result)

            val availableProducts = 0.until(products.length()).map {
                ProductDetails(products.getJSONObject(it))
            }

            return ResponseEntity(availableProducts, HttpStatus.OK)
        } catch (e: Throwable) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    private fun getFromAPI(url: String): String =
        RestTemplate().getForEntity(URI.create("$orderAPIUrl$url"), String::class.java).body?.trim() ?: ""

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

    fun getAuthToken(username: String, password: String): String {
        val uri = URI.create("$orderAPIUrl/auth")
        val headers = HttpHeaders()
        headers["Content-Type"] = "application/json"

        val request =
            RequestEntity("{\"username\": \"$username\", \"password\": \"$password\"}", headers, HttpMethod.POST, uri)
        val response = RestTemplate().exchange(request, String::class.java)
        return response.body ?: ""
    }
}