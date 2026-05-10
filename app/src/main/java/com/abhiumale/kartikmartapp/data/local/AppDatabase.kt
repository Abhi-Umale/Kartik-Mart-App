package com.abhiumale.kartikmartapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abhiumale.kartikmartapp.data.local.dao.CartDao
import com.abhiumale.kartikmartapp.data.local.dao.ProductDao
import com.abhiumale.kartikmartapp.data.local.entity.CartEntity
import com.abhiumale.kartikmartapp.data.local.entity.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        CartEntity::class
    ],
    version = 4,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun cartDao(): CartDao

}