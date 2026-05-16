package com.abhiumale.kartikmartapp.ui.presentation.notification

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiumale.kartikmartapp.domain.model.NotificationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _notifications = mutableStateOf<List<NotificationData>>(emptyList())
    val notifications: State<List<NotificationData>> = _notifications

    private val _unreadCount = mutableIntStateOf(0)
    val unreadCount: State<Int> = _unreadCount

    init {
        listenToNotifications()
    }

    private fun listenToNotifications() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("notifications")
            .whereEqualTo("userId", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                
                val list = snapshot?.documents?.mapNotNull { 
                    it.toObject(NotificationData::class.java)?.copy(id = it.id)
                } ?: emptyList()
                
                _notifications.value = list
                _unreadCount.intValue = list.count { !it.isRead }
            }
    }

    fun markAsRead(notificationId: String) {
        firestore.collection("notifications").document(notificationId)
            .update("isRead", true)
    }

    fun markAllAsRead() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("notifications")
            .whereEqualTo("userId", uid)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()
                snapshot.documents.forEach { 
                    batch.update(it.reference, "isRead", true)
                }
                batch.commit()
            }
    }
}
