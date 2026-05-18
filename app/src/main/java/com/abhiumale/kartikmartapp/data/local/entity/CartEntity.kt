package com.abhiumale.kartikmartapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Double,
    val mrp: Double,
    val image: String,
    val quantity: Int = 1,
    val category: String = "",
    val weight: String = ""
)