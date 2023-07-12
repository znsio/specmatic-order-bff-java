package com.component.orders.controllers

import com.component.orders.models.OrderResponse
import com.component.orders.models.OrderRequest
import com.component.orders.models.Product
import com.component.orders.services.OrderBFFService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class WebsiteAPI {

    @Autowired
    lateinit var orderBFFService: OrderBFFService

    @PostMapping("/orders", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createOrder(@RequestBody orderRequest: OrderRequest): OrderResponse {
        return orderBFFService.createOrder(orderRequest)
    }

    @GetMapping("/findAvailableProducts", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAvailableProducts(
        @RequestParam(
            name = "type",
            required = true
        ) type: String
    ): ResponseEntity<List<Product>> {
        val availableProducts = orderBFFService.findProducts(type)
        return ResponseEntity(availableProducts, HttpStatus.OK)
    }

}