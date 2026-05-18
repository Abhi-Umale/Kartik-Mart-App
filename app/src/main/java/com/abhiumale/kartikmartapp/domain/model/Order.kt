package com.abhiumale.kartikmartapp.domain.model

import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@Serializable
@IgnoreExtraProperties
data class Order(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: String = "Pending", // Pending, Incoming, Out for Delivery, Delivered, Cancelled
    val timestamp: Long = System.currentTimeMillis(),
    val paymentMethod: String = "",
    val address: String = "",
    val phone: String = "",
    val deliveryOtp: String? = null
)
