package com.abhiumale.kartikmartapp.domain

import android.net.Uri

interface AuthRepository {
    suspend fun login(email: String, password: String)
    suspend fun register(email: String, password: String, name: String, phone: String)
    suspend fun sendPasswordReset(email: String)
    suspend fun getUserData(): Map<String, Any>?
    fun logout()
    fun isLoggedIn(): Boolean
    fun getCurrentUserId(): String?
    
    // Production level features
    suspend fun uploadProfileImage(uri: Uri): String
    suspend fun updateUserData(data: Map<String, Any>)
}
