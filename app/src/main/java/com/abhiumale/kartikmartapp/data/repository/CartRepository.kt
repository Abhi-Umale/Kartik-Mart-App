 package com.abhiumale.kartikmartapp.data.repository

import com.abhiumale.kartikmartapp.data.local.dao.CartDao
import com.abhiumale.kartikmartapp.data.local.entity.CartEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.first

// CartRepository.kt
class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    suspend fun addToCart(cart: CartEntity) {
        // .first() ka matlab hai Flow ki current snapshot le lo
        val existingItem = cartDao.getCartProducts().first().find { it.id == cart.id }

        if (existingItem == null) {
            cartDao.insertCart(cart)
        } else {
            cartDao.increaseQuantity(cart.id)
        }
    }

    suspend fun removeFromCart(productId: Int) {
        val existingItem = cartDao.getCartProducts().first().find { it.id == productId }

        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                cartDao.decreaseQuantity(productId)
            } else {
                cartDao.deleteProduct(productId)
            }
        }
    }
    suspend fun deleteProductDirectly(productId: Int) {
        cartDao.deleteProduct(productId)
    }
    suspend fun clearCart() {
        cartDao.clearCart()
    }
    fun getCartProducts() = cartDao.getCartProducts()
}