package com.component.orders.controllers

import com.component.orders.models.NewProduct
import com.component.orders.models.Product
import com.component.orders.services.OrderBFFService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller


@Controller
class ProductsGraphQL(@Autowired val orderBFFService: OrderBFFService) {
    @QueryMapping
    fun findAvailableProducts(@Argument type: String, @Argument pageSize: Int): List<Product> {
        if (pageSize < 0) throw IllegalArgumentException("pageSize must be positive")
        return orderBFFService.findProducts(type)
    }

    @QueryMapping
    fun search(@Argument name: String) = "response for $name"

    @QueryMapping
    fun greeting() = "Welcome!"

    @MutationMapping
    fun createProduct(@Argument newProduct: NewProduct): Product? {
        return orderBFFService.createProductAndReturn(newProduct)
    }
}
