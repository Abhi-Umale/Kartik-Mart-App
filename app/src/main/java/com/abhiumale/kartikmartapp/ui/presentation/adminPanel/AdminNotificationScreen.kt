package com.abhiumale.kartikmartapp.ui.presentation.adminPanel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    var notifications by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val snapshot = firestore.collection("admin_notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            notifications = snapshot.documents.mapNotNull { it.data?.plus("id" to it.id) }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Notifications") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (notifications.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No notifications", color = Color.Gray)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(notifications) { notif ->
                    NotificationItem(notif) {
                        // Navigate to Order Details if orderId exists
                        val orderId = notif["orderId"] as? String
                        if (orderId != null) {
                            navController.navigate(Routs.AdminOrdersRouts)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notif: Map<String, Any>, onClick: () -> Unit) {
    val title = notif["title"] as? String ?: "Notification"
    val message = notif["message"] as? String ?: ""
    val timestamp = notif["timestamp"] as? Long ?: 0L
    val isRead = notif["isRead"] as? Boolean ?: true

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isRead) Color.White else Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFFFFF9C4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFFF9800))
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(message, fontSize = 13.sp, color = Color.DarkGray, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                Text(sdf.format(Date(timestamp)), fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}
