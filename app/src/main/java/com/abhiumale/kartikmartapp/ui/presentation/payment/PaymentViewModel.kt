package com.abhiumale.kartikmartapp.ui.presentation.payment

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiumale.kartikmartapp.data.remote.FirebaseAuthSource
import com.abhiumale.kartikmartapp.domain.model.Order
import com.abhiumale.kartikmartapp.domain.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.abhiumale.kartikmartapp.ui.notification.NotificationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    fun placeOrder(orderId: String, amount: Double, paymentMethod: String, address: String? = null, onComplete: () -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser
            val uid = user?.uid ?: return@launch
            val userName = userData.value?.get("name")?.toString() ?: "User"
            val userPhone = userData.value?.get("phone")?.toString() ?: ""
            
            // Generate 4-digit OTP
            val otp = (1000..9999).random().toString()

            val userAddress = address ?: userData.value?.get("address")?.toString() ?: "No address found"

            val cartItems = checkoutRepository.getCartProducts()

            val order = Order(
                orderId = orderId,
                userId = uid,
                userName = userName,
                totalAmount = amount,
                status = "Accepted",
                paymentMethod = paymentMethod,
                items = cartItems,
                timestamp = System.currentTimeMillis(),
                address = userAddress,
                phone = userPhone,
                deliveryOtp = otp
            )

            try {
                firestore.collection("orders").document(orderId).set(order).await()
                sendOrderNotifications(orderId, amount, userName, userPhone, uid, paymentMethod)
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendOrderNotifications(orderId: String, amount: Double, userName: String, userPhone: String, userId: String, paymentMethod: String) {
        val userNotification = mapOf(
            "userId" to userId,
            "title" to if (paymentMethod == "COD") "Order Placed! 🛍️" else "Order & Payment Success! 🎉",
            "message" to "Order #$orderId of ₹$amount successful. Status: Pending.",
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
            "type" to "order"
        )
        firestore.collection("notifications").add(userNotification)

        NotificationUtils.showNotification(
            context,
            if (paymentMethod == "COD") "Order Placed! 🛍️" else "Order & Payment Success! 🎉",
            "Your order #$orderId of ₹$amount has been placed."
        )

        notifyAdmin(orderId, amount, userName, userPhone)
    }

    fun notifyAdmin(orderId: String, amount: Double, userName: String, userPhone: String) {
        val adminNotification = mapOf(
            "id" to "NT${System.currentTimeMillis()}",
            "title" to "New Order Received! 🛍️",
            "message" to "New order for ₹$amount from $userName ($userPhone)",
            "orderId" to orderId,
            "userName" to userName,
            "userPhone" to userPhone,
            "amount" to amount,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false
        )
        firestore.collection("admin_notifications").document(adminNotification["id"].toString()).set(adminNotification)
    }

    fun handlePaymentResult(success: Boolean, orderId: String, amount: Double, error: String? = null) {
        val uid = auth.currentUser?.uid ?: return
        val userName = userData.value?.get("name")?.toString() ?: "User"
        val userPhone = userData.value?.get("phone")?.toString() ?: ""

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
                "id" to "NT_PAY_${System.currentTimeMillis()}",
                "title" to "Payment Received! 💰",
                "message" to "₹$amount credited from $userName ($userPhone) for order #$orderId.",
                "timestamp" to System.currentTimeMillis(),
                "orderId" to orderId,
                "userName" to userName,
                "userPhone" to userPhone,
                "amount" to amount,
                "isRead" to false
            )
            firestore.collection("admin_notifications").document(adminPaymentNotif["id"].toString()).set(adminPaymentNotif)
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
}