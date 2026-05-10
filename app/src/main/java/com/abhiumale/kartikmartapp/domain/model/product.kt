package com.abhiumale.kartikmartapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    var id: Long = 0L,
    val name: String = "",
    val price: Double = 0.0,
    val mrp: Double = 0.0,
    val image: String = "",
    val category: String = "",
    val brand: String = "",
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val weight: String = ""
)
