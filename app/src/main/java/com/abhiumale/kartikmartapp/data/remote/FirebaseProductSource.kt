package com.abhiumale.kartikmartapp.data.remote

import android.util.Log
import com.abhiumale.kartikmartapp.domain.model.Product
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseProductSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val rtdb: FirebaseDatabase,
    private val storage: FirebaseStorage
) {

    private val TAG = "FIREBASE_SOURCE"

    suspend fun uploadProductImage(uri: android.net.Uri): String {
        val fileName = "products/${System.currentTimeMillis()}.jpg"
        val ref = storage.reference.child(fileName)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun getProducts(): List<Product> {
        val productMap = mutableMapOf<String, Product>()

        // 1. Fetch from Firestore
        try {
            val snapshot = db.collection("products").get().await()
            snapshot.documents.forEach { doc ->
                try {
                    // Try automatic mapping first
                    val p = doc.toObject(Product::class.java)
                    if (p != null) {
                        // Agar doc.id numeric nahi hai, toh hash code use karein ya manually manage karein
                        val finalId = doc.id.toLongOrNull() ?: p.id
                        val finalProduct = p.copy(id = finalId)

                        productMap[finalId.toString()] = finalProduct
                    }
                } catch (e: Exception) {
                    // Manual fallback agar toObject fail ho jaye
                    val manualProduct = Product(
                        id = doc.id.toLongOrNull() ?: 0L,
                        name = doc.getString("name") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        image = doc.getString("image") ?: "",
                        category = doc.getString("category") ?: ""
                    )
                    if (manualProduct.name.isNotEmpty()) {
                        productMap[manualProduct.id.toString()] = manualProduct
                    }
                    Log.e(TAG, "Manual Mapping used for: ${doc.id}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firestore fetch error: ${e.message}")
        }

        // 2. Fetch from Realtime Database
        try {
            val snapshot = rtdb.getReference("products").get().await()
            snapshot.children.forEach { child ->
                try {
                    val p = child.getValue(Product::class.java)
                    if (p != null) {
                        val finalProduct = if (p.id == 0L) {
                            p.copy(id = child.key?.toLongOrNull() ?: 0L)
                        } else p
                        // Only add if not already in map from Firestore
                        if (!productMap.containsKey(finalProduct.id.toString())) {
                            productMap[finalProduct.id.toString()] = finalProduct
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping RTDB child ${child.key}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "RTDB fetch error: ${e.message}")
        }

        return productMap.values.toList()
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        return getProducts().filter { it.category.equals(category, ignoreCase = true) }
    }

    suspend fun getProductsByBrand(brand: String): List<Product> {
        return getProducts().filter { it.brand.equals(brand, ignoreCase = true) }
    }

    suspend fun addProduct(product: Product) {
        try {
            // 1. Save to Firestore
            db.collection("products").document(product.id.toString()).set(product).await()
            
            // 2. Save to Realtime Database
            rtdb.getReference("products").child(product.id.toString()).setValue(product).await()
            
            Log.d(TAG, "Product added successfully to both Firestore and RTDB")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding product: ${e.message}")
            throw e
        }
    }

    suspend fun deleteProduct(productId: Long) {
        try {
            db.collection("products").document(productId.toString()).delete().await()
            rtdb.getReference("products").child(productId.toString()).removeValue().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting product: ${e.message}")
        }
    }

    suspend fun updateProduct(product: Product) {
        addProduct(product) // Same logic for update
    }

    suspend fun getAllUsers(): List<Map<String, Any>> {
        return try {
            val snapshot = db.collection("users").get().await()
            snapshot.documents.mapNotNull { it.data?.plus("uid" to it.id) }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching users: ${e.message}")
            emptyList()
        }
    }

    suspend fun toggleUserBlockStatus(userId: String, isBlocked: Boolean) {
        try {
            db.collection("users").document(userId).update("isBlocked", isBlocked).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling block status: ${e.message}")
        }
    }

    suspend fun getProductCount(): Int {
        return getProducts().size
    }

    suspend fun getUserCount(): Int {
        return try {
            val snapshot = db.collection("users").get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getAllOrders(): List<com.abhiumale.kartikmartapp.domain.model.Order> {
        return try {
            val snapshot = db.collection("orders").get().await()
            snapshot.documents.mapNotNull { it.toObject(com.abhiumale.kartikmartapp.domain.model.Order::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching orders: ${e.message}")
            emptyList()
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        try {
            db.collection("orders").document(orderId).update("status", status).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order status: ${e.message}")
        }
    }
}
