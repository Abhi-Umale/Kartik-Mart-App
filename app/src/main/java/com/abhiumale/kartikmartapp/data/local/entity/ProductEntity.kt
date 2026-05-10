package com.abhiumale.kartikmartapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(

    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Double,
    val image: String,
    val category: String,
    val brand: String?,
    val rating: Double?,
    val weight: String?

)