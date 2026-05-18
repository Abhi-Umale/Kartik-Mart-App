package com.abhiumale.kartikmartapp.data.repository

import com.abhiumale.kartikmartapp.data.local.dao.CartDao
import com.abhiumale.kartikmartapp.domain.model.CartItem
import com.abhiumale.kartikmartapp.domain.model.Order
import com.abhiumale.kartikmartapp.domain.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CheckoutRepository @Inject constructor(
    private val db: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val cartDao: CartDao
) {
    private val userId get() = auth.currentUser?.uid ?: ""

    suspend fun getCartProducts(): List<CartItem> {
        return try {
            cartDao.getCartProducts().first().map {
                CartItem(
                    productId = it.id.toString(),
                    name = it.name,
                    imageUrl = it.image,
                    price = it.price.toInt(),
                    mrp = it.mrp.toInt(),
                    quantity = it.quantity,
                    category = it.category,
                    weight = it.weight
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSingleProduct(productId: String): CartItem? {
        return try {
            val firestoreSnapshot = firestore.collection("products").document(productId).get().await()
            val productFromFirestore = firestoreSnapshot.toObject(Product::class.java)

            if (productFromFirestore != null) {
                return CartItem(
                    productId = if (productFromFirestore.id == 0L) productId else productFromFirestore.id.toString(),
                    name = productFromFirestore.name,
                    imageUrl = productFromFirestore.image,
                    price = productFromFirestore.price.toInt(),
                    mrp = productFromFirestore.mrp.toInt(),
                    quantity = 1,
                    weight = productFromFirestore.weight,
                    category = productFromFirestore.category
                )
            }

            val snapshot = db.getReference("products/$productId").get().await()
            val product = snapshot.getValue(Product::class.java)
            product?.let {
                CartItem(
                    productId = if (it.id == 0L) productId else it.id.toString(),
                    name = it.name,
                    imageUrl = it.image,
                    price = it.price.toInt(),
                    mrp = it.mrp.toInt(),
                    quantity = 1,
                    weight = it.weight,
                    category = it.category
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun placeOrder(order: Order) {
        // 1. Save to Firestore
        firestore.collection("orders").document(order.orderId).set(order).await()

        // 2. Clear Cart
        cartDao.clearCart()

        // 3. Admin Notification
        val notificationId = "NT${System.currentTimeMillis()}"
        val notification = mapOf(
            "id" to notificationId,
            "title" to "New Order Received!",
            "message" to "Order for ₹${order.totalAmount} by ${order.userName}",
            "orderId" to order.orderId,
            "userName" to order.userName,
            "userPhone" to order.phone,
            "amount" to order.totalAmount,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false
        )
        firestore.collection("admin_notifications").document(notificationId).set(notification).await()
    }

    fun getLiveLocation(orderId: String) = callbackFlow<LatLng> {
        if (orderId.isBlank()) {
            trySend(LatLng(18.5204, 73.8567))
            close()
            return@callbackFlow
        }

        val ref = db.getReference("delivery_location/$orderId")
        val listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                val lat = s.child("lat").getValue(Double::class.java) ?: 0.0
                val lng = s.child("lng").getValue(Double::class.java) ?: 0.0
                trySend(LatLng(lat, lng))
            }
            override fun onCancelled(e: DatabaseError) {
                close()
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}