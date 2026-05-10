package com.abhiumale.kartikmartapp.ui.presentation.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiumale.kartikmartapp.domain.model.Product
import com.abhiumale.kartikmartapp.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.flow

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    val userName = flow {
        emit(FirebaseAuth.getInstance().currentUser?.displayName ?: "User")
    }

    var productList by mutableStateOf<List<Product>>(emptyList())
        private set

    init {
        loadProducts()
    }
    private fun loadProducts() {
        viewModelScope.launch {
            productList = getProductsUseCase()
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            val allProducts = getProductsUseCase()
            productList = allProducts.filter {
                it.category.equals(category, ignoreCase = true)
            }
        }
    }
    fun filterByBrand(brand: String) {
        viewModelScope.launch {
            productList = getProductsUseCase.byBrand(brand)
        }
    }

    fun loadAllProducts() {
        viewModelScope.launch {
            productList = getProductsUseCase()
        }
    }
}