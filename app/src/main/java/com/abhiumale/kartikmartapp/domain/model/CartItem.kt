package com.abhiumale.kartikmartapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val price: Int = 0,
    val mrp: Int = 0,
    val quantity: Int = 1,
    val weight: String = "",
    val category: String = ""
)