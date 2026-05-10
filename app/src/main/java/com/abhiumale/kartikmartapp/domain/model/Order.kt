package com.abhiumale.kartikmartapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: String = "Pending", // Pending, Delivered, Cancelled
    val timestamp: Long = System.currentTimeMillis()
)
