package com.component.orders.models

import org.springframework.http.HttpMethod

enum class API(val method: HttpMethod, val url: String) {
    CREATE_ORDER(HttpMethod.POST, "/orders")
}