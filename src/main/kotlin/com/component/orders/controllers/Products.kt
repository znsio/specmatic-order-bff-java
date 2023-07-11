package com.component.orders.controllers

import com.component.orders.models.CreationStatus
import com.component.orders.models.ProductDetails
import com.component.orders.services.ProductService
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class WebsiteAPI {

    @Autowired
    lateinit var productService: ProductService

    @PostMapping("/orders", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createOrder(@RequestBody order: String): CreationStatus {
        return productService.createOrder(JSONObject(order))
    }

    @GetMapping("/findAvailableProducts", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAvailableProducts(
        @RequestParam(
            name = "type",
            required = true
        ) type: String
    ): ResponseEntity<List<ProductDetails>> {
        try {
            val availableProducts = productService.findProducts(type)
            return ResponseEntity(availableProducts, HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}