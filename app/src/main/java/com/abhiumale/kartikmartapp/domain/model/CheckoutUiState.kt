package com.abhiumale.kartikmartapp.domain.model

data class CheckoutUiState(
    val products: List<CartItem> = emptyList(),
    val mrp: Double = 0.0,
    val savings: Int = 0,
    val totalPay: Int = 0,
    val deliveryFee: Int = 49,
    val isHomeDelivery: Boolean = true,
    val userAddress: String = "Fetching address...",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)