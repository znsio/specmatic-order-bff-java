package com.component.orders.controllers

import com.component.orders.models.Id
import com.component.orders.models.NewProduct
import com.component.orders.models.Product
import com.component.orders.services.OrderBFFService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Products(@Autowired val orderBFFService: OrderBFFService) {
    @GetMapping("/findAvailableProducts", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAvailableProducts(
        @RequestParam(
            name = "type",
            required = false,
            defaultValue = "gadget"
        ) type: String,
        @RequestHeader(
            name = "pageSize",
            required = true
        ) pageSize: Int
    ): ResponseEntity<List<Product>> {
        if (pageSize < 0) throw IllegalArgumentException("pageSize must be positive")
        return ResponseEntity(orderBFFService.findProducts(type), HttpStatus.OK)
    }

    @PostMapping("/products", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createProduct(
        @RequestBody newProduct: NewProduct
    ): ResponseEntity<Id> = ResponseEntity(orderBFFService.createProduct(newProduct), HttpStatus.CREATED)
}