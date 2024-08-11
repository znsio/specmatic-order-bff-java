package com.component.orders.models

import java.util.concurrent.atomic.AtomicInteger
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class Product(
    @field:NotNull val name: String = "",
    @field:NotNull val type: String = "gadget",
    @field:Positive val inventory: Int = 0,
    val id: Int = idGenerator.getAndIncrement()
) {
    companion object {
        val idGenerator: AtomicInteger = AtomicInteger()
    }
}
