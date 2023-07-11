package com.component.orders.services

import com.component.orders.models.CreationStatus
import com.component.orders.models.ProductDetails
import org.json.JSONObject

interface ProductService {
    fun createOrder(orderJSON: JSONObject): CreationStatus
    fun findProducts(type:String): List<ProductDetails>
}