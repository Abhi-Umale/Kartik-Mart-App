package com.abhiumale.kartikmartapp.data.repository

import android.net.Uri
import com.abhiumale.kartikmartapp.data.remote.FirebaseAuthSource
import com.abhiumale.kartikmartapp.domain.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebase: FirebaseAuthSource
) : AuthRepository {

    override suspend fun login(email: String, password: String) {
        firebase.login(email, password)
    }

    override suspend fun register(email: String, password: String, name: String, phone: String) {
        firebase.register(email, password, name, phone)
    }

    override suspend fun getUserData(): Map<String, Any>? {
        return firebase.getUserData()
    }

    override suspend fun updateUserData(data: Map<String, Any>) {
        firebase.updateUserData(data)
    }

    override suspend fun uploadProfileImage(uri: Uri): String {
        return firebase.uploadImage(uri)
    }

    override suspend fun sendPasswordReset(email: String) {
        firebase.sendPasswordReset(email)
    }

    override fun logout() { firebase.logout() }
    override fun isLoggedIn(): Boolean { return firebase.isLoggedIn() }
    override fun getCurrentUserId(): String? = firebase.getCurrentUserId()
}
