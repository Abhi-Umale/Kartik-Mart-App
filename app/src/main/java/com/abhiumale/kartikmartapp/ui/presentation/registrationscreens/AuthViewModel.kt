package com.abhiumale.kartikmartapp.ui.presentation.registrationscreens

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiumale.kartikmartapp.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var userRole by mutableStateOf<String?>(null)
        private set
    var loginSuccess by mutableStateOf(false)
        private set
    var loginError by mutableStateOf<String?>(null)
        private set
    var registerSuccess by mutableStateOf(false)
        private set
    var registerError by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var userData by mutableStateOf<Map<String, Any>?>(null)
        private set
    var resetPasswordSuccess by mutableStateOf(false)
        private set

    fun clearState() {
        loginSuccess = false
        registerSuccess = false
        loginError = null
        registerError = null
        isLoading = false
        resetPasswordSuccess = false
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                Log.d("AUTH_DEBUG", "Starting Firebase Auth login for: $email")
                repository.login(email, password)
                // Fetch user data and role
                val data = withTimeoutOrNull(10000) { // Increased timeout to 10s
                    repository.getUserData()
                }

                if (data != null) {
                    userData = data
                    userRole = data["role"] as? String ?: "USER"
                    Log.d("AUTH_DEBUG", "User data fetched. Role: $userRole")
                    loginSuccess = true
                } else {
                    Log.w("AUTH_DEBUG", "User data not found in database")
                    // If user is authenticated in Auth but no data in DB, we should handle it
                    // For now, let's treat as success but default to USER role
                    userRole = "USER"
                    loginSuccess = true
                }
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Login error: ${e.message}")
                loginError = e.message ?: "Login failed. Please check credentials."
            } finally {
                isLoading = false
            }
        }
    }

    fun register(email: String, password: String, name: String, phone: String) {
        viewModelScope.launch {
            isLoading = true
            registerError = null
            try {
                repository.register(email, password, name, phone)
                registerSuccess = true
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Registration error: ${e.message}")
                registerError = e.message ?: "Registration failed"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchUserData() {
        if (!isLoggedIn()) return

        viewModelScope.launch {
            isLoading = true
            try {
                val data = withTimeoutOrNull(10000) { repository.getUserData() }
                if (data != null) {
                    userData = data
                    userRole = data["role"] as? String ?: "USER"
                } else {
                    Log.w("AUTH_DEBUG", "User data fetch returned null")
                    // Do NOT logout automatically on one failed fetch, could be network
                }
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Fetch error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            isLoading = true
            try {
                repository.uploadProfileImage(uri)
                val data = repository.getUserData()
                userData = data
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Upload error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                repository.sendPasswordReset(email)
                resetPasswordSuccess = true
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Reset error: ${e.message}")
            }
        }
    }

    fun logout() {
        repository.logout()
        userData = null
        userRole = null
        loginSuccess = false
        clearState()
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
}
