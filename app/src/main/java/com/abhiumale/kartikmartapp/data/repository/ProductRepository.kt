package com.abhiumale.kartikmartapp.data.repository

import com.abhiumale.kartikmartapp.data.remote.FirebaseProductSource
import com.abhiumale.kartikmartapp.domain.model.Product
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val firebase: FirebaseProductSource
) {

    suspend fun getProducts(): List<Product> {
        return firebase.getProducts()
    }

    suspend fun getByCategory(category: String): List<Product> {
        return firebase.getProductsByCategory(category)
    }

    suspend fun getByBrand(brand: String): List<Product> {
        return firebase.getProductsByBrand(brand)
    }

    suspend fun addProduct(product: Product) = firebase.addProduct(product)
    suspend fun deleteProduct(productId: Long) = firebase.deleteProduct(productId)
    suspend fun updateProduct(product: Product) = firebase.updateProduct(product)
    suspend fun getAllUsers() = firebase.getAllUsers()
    suspend fun toggleUserBlockStatus(userId: String, isBlocked: Boolean) = firebase.toggleUserBlockStatus(userId, isBlocked)
    suspend fun getProductCount() = firebase.getProductCount()
    suspend fun getUserCount() = firebase.getUserCount()
    suspend fun uploadProductImage(uri: android.net.Uri) = firebase.uploadProductImage(uri)

    suspend fun getAllOrders() = firebase.getAllOrders()
    suspend fun updateOrderStatus(orderId: String, status: String) = firebase.updateOrderStatus(orderId, status)
}