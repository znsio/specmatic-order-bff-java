package com.component.orders.models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class NewProduct(
    @field:NotNull @field:JsonDeserialize(using = StrictStringDeserializer::class) val name: String = "",
    @field:NotNull val type: String = "gadget",
    @field:NotNull
    @field:Min(1)
    @field:Max(101)
    val inventory: Int? = 1
)
