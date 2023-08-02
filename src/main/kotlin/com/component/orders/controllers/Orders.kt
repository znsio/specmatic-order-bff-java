package com.component.orders.controllers

import com.component.orders.models.OrderRequest
import com.component.orders.models.OrderResponse
import com.component.orders.services.OrderBFFService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Orders(@Autowired val orderBFFService: OrderBFFService) {
    @PostMapping("/orders", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createOrder(@RequestBody orderRequest: OrderRequest): OrderResponse = orderBFFService.createOrder(orderRequest)
}