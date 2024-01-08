package com.component.orders.models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class NewProduct(
    @field:NotNull @field:JsonDeserialize(using = StrictStringDeserializer::class) val name: String = "",
    @field:NotNull val type: String = "gadget",
    @field:NotNull @field:Positive val inventory: Int? = 1
)
