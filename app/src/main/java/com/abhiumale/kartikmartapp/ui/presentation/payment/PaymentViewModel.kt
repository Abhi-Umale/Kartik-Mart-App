package com.abhiumale.kartikmartapp.ui.presentation.payment

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiumale.kartikmartapp.data.remote.FirebaseAuthSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.abhiumale.kartikmartapp.ui.notification.NotificationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val authSource: FirebaseAuthSource,
    private val checkoutRepository: com.abhiumale.kartikmartapp.data.repository.CheckoutRepository
) : ViewModel() {

    private val _userData = mutableStateOf<Map<String, Any>?>(null)
    val userData: State<Map<String, Any>?> = _userData

    init {
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            _userData.value = authSource.getUserData()
        }
    }

    fun placeOrder(orderId: String, amount: Double, paymentMethod: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val userName = userData.value?.get("name")?.toString() ?: "User"
            
            // Fetch cart products to include in order
            val cartItems = checkoutRepository.getCartProducts()

            val order = mapOf(
                "orderId" to orderId,
                "userId" to uid,
                "userName" to userName,
                "totalAmount" to amount,
                "status" to "Pending", // Initially Pending for Admin
                "paymentMethod" to paymentMethod,
                "items" to cartItems.map { 
                    mapOf(
                        "productId" to it.productId,
                        "name" to it.name,
                        "quantity" to it.quantity,
                        "price" to it.price,
                        "image" to it.imageUrl
                    )
                },
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("orders").document(orderId)
                .set(order)
                .addOnSuccessListener { 
                    sendOrderNotifications(orderId, amount, userName, uid)
                    onComplete() 
                }
        }
    }

    private fun sendOrderNotifications(orderId: String, amount: Double, userName: String, userId: String) {
        val userNotification = mapOf(
            "userId" to userId,
            "title" to "Order Placed Successfully! 🎉",
            "message" to "Your order #$orderId of ₹$amount has been placed. We'll update you soon.",
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
            "type" to "order"
        )
        firestore.collection("notifications").add(userNotification)

        // Local Notification for immediate feedback
        NotificationUtils.showNotification(
            context,
            "Order Placed Successfully! 🎉",
            "Your order #$orderId of ₹$amount has been placed."
        )

        // Admin Notification
        notifyAdmin(orderId, amount, userName)
    }

    fun handlePaymentResult(success: Boolean, orderId: String, amount: Double, error: String? = null) {
        val uid = auth.currentUser?.uid ?: return
        val userName = userData.value?.get("name")?.toString() ?: "User"

        if (success) {
            val title = "Payment Successful! ✅"
            val message = "₹$amount has been debited for your order #$orderId."
            
            val userPaymentNotif = mapOf(
                "userId" to uid,
                "title" to title,
                "message" to message,
                "timestamp" to System.currentTimeMillis(),
                "isRead" to false,
                "type" to "payment"
            )
            firestore.collection("notifications").add(userPaymentNotif)
            
            NotificationUtils.showNotification(context, title, message)

            val adminPaymentNotif = mapOf(
                "title" to "Payment Received! 💰",
                "message" to "₹$amount credited from $userName for order #$orderId.",
                "timestamp" to System.currentTimeMillis(),
                "orderId" to orderId,
                "read" to false
            )
            firestore.collection("admin_notifications").add(adminPaymentNotif)
        } else {
            val title = "Payment Failed! ❌"
            val message = "Your payment for order #$orderId was unsuccessful."
            
            val userFailNotif = mapOf(
                "userId" to uid,
                "title" to title,
                "message" to "$message Error: ${error ?: "Unknown"}",
                "timestamp" to System.currentTimeMillis(),
                "isRead" to false,
                "type" to "payment"
            )
            firestore.collection("notifications").add(userFailNotif)
            
            NotificationUtils.showNotification(context, title, message)
        }
    }

    fun notifyAdmin(orderId: String, amount: Double, userName: String) {
        val adminNotification = mapOf(
            "message" to "New Order Received from $userName",
            "orderId" to orderId,
            "amount" to amount,
            "timestamp" to System.currentTimeMillis(),
            "read" to false
        )
        firestore.collection("admin_notifications").add(adminNotification)
    }

}