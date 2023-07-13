package com.component.orders.models.messages

data class ProductMessage(val id: Int, val name: String, val inventory: Int, val categories: List<ProductCategory> = emptyList())

