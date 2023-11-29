package com.component.orders.services

import com.component.orders.backend.OrderService
import com.component.orders.models.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderBFFService {

    @Autowired
    lateinit var orderService: OrderService

    fun createOrder(orderRequest: OrderRequest): OrderResponse {
        val orderId = orderService.createOrder(orderRequest)
        return OrderResponse(id = orderId)
    }

    fun findProducts(type: String): List<Product> {
        return orderService.findProducts(type)
    }

    fun createProduct(newProduct: NewProduct): Id {
        return Id(orderService.createProduct(newProduct))
    }
}