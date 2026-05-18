package com.abhiumale.kartikmartapp.ui.presentation.viewmodel

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.abhiumale.kartikmartapp.data.repository.CheckoutRepository
import com.abhiumale.kartikmartapp.domain.model.CartItem
import com.abhiumale.kartikmartapp.domain.model.CheckoutUiState
import com.abhiumale.kartikmartapp.domain.model.Order
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: CheckoutRepository,
    private val auth: FirebaseAuth,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    private val args = savedStateHandle.toRoute<Routs.CheckoutScreen>()

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val productId = args.productId
                val result = if (productId == null || productId == "null" || productId.isBlank()) {
                    repository.getCartProducts()
                } else {
                    val singleProduct = repository.getSingleProduct(productId)

                    if (singleProduct != null) {
                        listOf(singleProduct)
                    } else {
                        android.util.Log.e("CHECKOUT_VM", "Single product NOT found for ID: $productId")
                        emptyList()
                    }
                }

                if (result.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "No products found", products = emptyList()) }
                } else {
                    calculatePrices(result)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun calculatePrices(items: List<CartItem>) {
        val totalMrp = items.sumOf { it.mrp * it.quantity }
        val totalCurrentPrice = items.sumOf { it.price * it.quantity }
        val totalSavings = (totalMrp - totalCurrentPrice)

        _uiState.update { state ->
            state.copy(
                products = items,
                mrp = totalMrp.toDouble(),
                savings = totalSavings,
                totalPay = (totalCurrentPrice + state.deliveryFee),
                isLoading = false
            )
        }
    }

    fun updateDeliveryMode(isHome: Boolean) {
        _uiState.update { state ->
            val newFee = if (isHome) 49 else 0
            val newAddress = if (isHome) {
                if (state.userAddress.isBlank() || state.userAddress.contains("HQ")) "Fetching address..." else state.userAddress
            } else {
                "Kartik Mart HQ, Nagpur Road, Amravati"
            }

            state.copy(
                isHomeDelivery = isHome,
                deliveryFee = newFee,
                userAddress = newAddress
            )
        }
        calculatePrices(_uiState.value.products)
    }

    fun updateAddress(newAddress: String) {
        _uiState.update { it.copy(userAddress = newAddress) }
    }

    fun fetchUserLocation(context: Context) {
        val appContext = context.applicationContext
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    updateAddressFromLocation(appContext, LatLng(it.latitude, it.longitude))
                }
            }
        } catch (e: SecurityException) {
            _uiState.update { it.copy(errorMessage = "Permission denied ${e.message}") }
        }
    }

    fun updateAddressFromLocation(context: Context, latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) return@launch

                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val addressLine = addresses?.firstOrNull()?.getAddressLine(0) ?: "Address not found"

                withContext(Dispatchers.Main) {
                    updateAddress(addressLine)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(errorMessage = "Failed to fetch address: ${e.message}") }
                }
            }
        }
    }

    fun placeOrder(orderId: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = auth.currentUser
                // Normally we'd fetch extra user data from Firestore (phone etc.)
                // But for now let's use what we have or placeholder
                val order = Order(
                    orderId = orderId,
                    userId = user?.uid ?: "",
                    userName = user?.displayName ?: "User",
                    items = _uiState.value.products,
                    totalAmount = _uiState.value.totalPay.toDouble(),
                    status = "Pending",
                    timestamp = System.currentTimeMillis(),
                    paymentMethod = "Online",
                    address = _uiState.value.userAddress,
                    phone = user?.phoneNumber ?: ""
                )
                repository.placeOrder(order)
                onComplete(orderId)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}