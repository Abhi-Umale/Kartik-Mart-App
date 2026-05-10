package com.abhiumale.kartikmartapp.ui.presentation.adminPanel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiumale.kartikmartapp.data.repository.ProductRepository
import com.abhiumale.kartikmartapp.domain.model.Order
import com.abhiumale.kartikmartapp.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    var productCount by mutableStateOf(0)
    var userCount by mutableStateOf(0)
    var users by mutableStateOf<List<Map<String, Any>>>(emptyList())
    var allProducts by mutableStateOf<List<Product>>(emptyList())
    var orders by mutableStateOf<List<Order>>(emptyList())
    var categories by mutableStateOf<List<String>>(emptyList())
    var brands by mutableStateOf<List<String>>(emptyList())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchDashboardData()
        fetchProducts()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Sirf count lene ke bajaye list fetch karke size check karein
                val products = repository.getProducts()
                productCount = products.size
                allProducts = products // Sync dashboard list
                userCount = repository.getUserCount()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            isLoading = true
            try {
                allProducts = repository.getProducts()
                categories = allProducts.map { it.category }.distinct().filter { it.isNotEmpty() }
                brands = allProducts.map { it.brand }.distinct().filter { it.isNotEmpty() }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchOrders() {
        viewModelScope.launch {
            isLoading = true
            try {
                orders = repository.getAllOrders()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, status)
                fetchOrders()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun fetchUsers() {
        viewModelScope.launch {
            isLoading = true
            try {
                users = repository.getAllUsers()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleUserBlock(userId: String, currentBlocked: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleUserBlockStatus(userId, !currentBlocked)
                fetchUsers()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun addProduct(product: Product, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                repository.addProduct(product)
                onSuccess()
                fetchProducts() // Refresh products list
                fetchDashboardData()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
                fetchProducts()
                fetchDashboardData()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun uploadImage(uri: android.net.Uri, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val url = repository.uploadProductImage(uri)
                onComplete(url)
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
