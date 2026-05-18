package com.abhiumale.kartikmartapp.ui.presentation.orderscreen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiumale.kartikmartapp.domain.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val TAG = "OrdersViewModel"

    private val _orders = mutableStateOf<List<Order>>(emptyList())
    val orders: State<List<Order>> = _orders

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        fetchOrders()
    }

    fun fetchOrders() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            if (uid == null) {
                _errorMessage.value = "User not logged in"
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d(TAG, "Fetching orders for UID: $uid")
                
                // Note: This query requires a composite index in Firestore:
                // Collection: orders, Fields: userId (Asc), timestamp (Desc)
                val snapshot = firestore.collection("orders")
                    .whereEqualTo("userId", uid)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                Log.d(TAG, "Orders found: ${snapshot.size()}")
                
                val orderList = snapshot.toObjects(Order::class.java)
                _orders.value = orderList
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching orders: ${e.message}", e)
                _errorMessage.value = "Failed to load orders: ${e.message}"
                
                // Specific handling for missing index error
                if (e.message?.contains("index") == true) {
                    Log.e(TAG, "Missing Firestore Index! Check the link in the error message above.")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
