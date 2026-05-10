package com.abhiumale.kartikmartapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abhiumale.kartikmartapp.data.local.entity.CartEntity

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    @Query("SELECT * FROM cart")
    fun getCartProducts(): kotlinx.coroutines.flow.Flow<List<CartEntity>>
    // 1. Specific product ko bina quantity check kiye poora hatane ke liye
    @Query("DELETE FROM cart WHERE id = :productId")
    suspend fun deleteProduct(productId: Int)

    @Query("DELETE FROM cart")
    suspend fun clearCart()

    @Query("UPDATE cart SET quantity = quantity + 1 WHERE id = :productId")
    suspend fun increaseQuantity(productId: Int)

    @Query("UPDATE cart SET quantity = quantity - 1 WHERE id = :productId AND quantity > 1")
    suspend fun decreaseQuantity(productId: Int)

}