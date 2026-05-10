package com.abhiumale.kartikmartapp.data.repository

import com.abhiumale.kartikmartapp.data.local.dao.CartDao
import com.abhiumale.kartikmartapp.domain.model.CartItem
import com.abhiumale.kartikmartapp.domain.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CheckoutRepository @Inject constructor(
    private val db: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val cartDao: CartDao
) {
    // Current user ki ID nikalne ke liye
    private val userId get() = auth.currentUser?.uid ?: ""

    // Cart se products fetch karne ka function (Local Room DB se for consistency)
    suspend fun getCartProducts(): List<CartItem> {
        return try {
            cartDao.getCartProducts().first().map {
                CartItem(
                    productId = it.id.toString(),
                    name = it.name,
                    imageUrl = it.image,
                    price = it.price.toInt(),
                    mrp = it.mrp.toInt(),
                    quantity = it.quantity
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Single product fetch karne ka function (Buy Now scenario)
    suspend fun getSingleProduct(productId: String): CartItem? {
        return try {
            // Hum products node se data le rahe hain jo Product class use karta hai
            val snapshot = db.getReference("products/$productId").get().await()
            val product = snapshot.getValue(Product::class.java)
            product?.let {
                CartItem(
                    productId = it.id.toString(),
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

    // Live Tracking ke liye (Jaisa aapne pehle banaya tha)
    fun getLiveLocation(orderId: String) = callbackFlow<LatLng> {
        val ref = db.getReference("delivery_location/$orderId")
        val listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                val lat = s.child("lat").getValue(Double::class.java) ?: 0.0
                val lng = s.child("lng").getValue(Double::class.java) ?: 0.0
                trySend(LatLng(lat, lng))
            }
            override fun onCancelled(e: DatabaseError) { close(e.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}