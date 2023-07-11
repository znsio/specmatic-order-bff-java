package com.component.orders.models

import org.json.JSONObject

data class ProductDetails(val id: Int, val name: String, val inventory: Int, val type: String) {
    constructor(json: JSONObject): this(json.getInt("id"), json.getString("name"), json.getInt("inventory"), json.getString("type"))
}