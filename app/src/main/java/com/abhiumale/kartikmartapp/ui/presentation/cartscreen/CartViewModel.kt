package com.abhiumale.kartikmartapp.ui.presentation.cartscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abhiumale.kartikmartapp.data.local.DatabaseProvider
import com.abhiumale.kartikmartapp.data.repository.CartRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.abhiumale.kartikmartapp.data.local.entity.CartEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {

    var cartItems by mutableStateOf<List<CartEntity>>(emptyList())
        private set

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            // repository.getCartProducts() ab ek Flow hai
            repository.getCartProducts().collect { items ->
                cartItems = items
            }
        }
    }

    fun addToCart(product: CartEntity) {
        viewModelScope.launch {
            repository.addToCart(product)
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    // CartViewModel.kt ke andar add karein
    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart() // Ise repository mein banayenge
        }
    }

    // Full remove function (quantity 1-1 kam karne ke bajaye direct delete)
    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            repository.deleteProductDirectly(productId) // Ise repository mein banayenge
        }
    }

}