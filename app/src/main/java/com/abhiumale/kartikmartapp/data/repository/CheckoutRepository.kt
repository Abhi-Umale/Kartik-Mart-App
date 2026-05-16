package com.abhiumale.kartikmartapp.data.repository

import com.abhiumale.kartikmartapp.data.local.dao.CartDao
import com.abhiumale.kartikmartapp.domain.model.CartItem
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
    // Current user ki ID fetch
    private val userId get() = auth.currentUser?.uid ?: ""

    // Cart se products fetch ka function (Local Room DB se for consistency)
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
                    weight = ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Single product fetch ka function (Buy Now scenario)
    suspend fun getSingleProduct(productId: String): CartItem? {
        return try {
            // 1. Try Firestore first
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
                    weight = productFromFirestore.weight
                )
            }

            // 2. Fallback to Realtime Database
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
                    weight = it.weight
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    // Live Tracking ke liye
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
                android.util.Log.e("FirebaseTracking", "Permission denied or cancelled: ${e.message}")
                close()
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}