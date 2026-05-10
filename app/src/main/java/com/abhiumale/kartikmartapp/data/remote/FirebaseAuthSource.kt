package com.abhiumale.kartikmartapp.data.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(email: String, password: String, name: String, phone: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val userId = result.user?.uid ?: throw Exception("User ID null")

        val userMap = mapOf(
            "name" to name,
            "phone" to phone,
            "email" to email,
            "role" to "USER",
            "uid" to userId,
            "createdAt" to System.currentTimeMillis()
        )
        
        // Save to BOTH for production safety
        firestore.collection("users").document(userId).set(userMap).await()
        database.getReference("users").child(userId).setValue(userMap).await()
    }

    suspend fun getUserData(): Map<String, Any>? {
        val userId = auth.currentUser?.uid ?: return null
        android.util.Log.d("AUTH_DEBUG", "Fetching data for UID: $userId")
        
        // 1. Try Firestore First
        try {
            android.util.Log.d("AUTH_DEBUG", "Checking Firestore...")
            val firestoreData = firestore.collection("users").document(userId).get().await().data
            if (firestoreData != null) {
                android.util.Log.d("AUTH_DEBUG", "Firestore Role: ${firestoreData["role"]}")
                return firestoreData
            }
        } catch (e: Exception) {
            android.util.Log.e("AUTH_DEBUG", "Firestore Error: ${e.message}")
        }

        // 2. Fallback to Realtime Database
        return try {
            val snapshot = database.getReference("users").child(userId).get().await()
            // RTDB data ko safely map mein convert karein
            val data = snapshot.value as? Map<*, *>
            val result = data?.mapKeys { it.key.toString() }?.mapValues { it.value ?: "" }

            android.util.Log.d("AUTH_DEBUG", "RTDB Role: ${result?.get("role")}")
            result as? Map<String, Any>
        } catch (e: Exception) {
            android.util.Log.e("AUTH_DEBUG", "RTDB Error: ${e.message}")
            null
        }
    }

    suspend fun updateUserData(data: Map<String, Any>) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        firestore.collection("users").document(userId).update(data).await()
        database.getReference("users").child(userId).updateChildren(data).await()
    }

    suspend fun uploadImage(uri: Uri): String {
        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val storageRef = storage.reference.child("profile_pics/$uid.jpg")

        storageRef.putFile(uri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()

        updateUserData(mapOf("profileImage" to downloadUrl))
        return downloadUrl
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() { auth.signOut() }
    fun isLoggedIn(): Boolean = auth.currentUser != null
    fun getCurrentUserId(): String? = auth.currentUser?.uid
}
