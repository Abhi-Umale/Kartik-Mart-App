package com.abhiumale.kartikmartapp.ui.presentation.adminPanel

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.domain.model.Order
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending", "Accepted", "Delivered")

    LaunchedEffect(Unit) {
        viewModel.fetchOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            val filteredOrders = when (selectedTab) {
                0 -> viewModel.orders.filter { it.status == "Pending" }
                1 -> viewModel.orders.filter { it.status == "Accepted" || it.status == "Incoming" }
                else -> viewModel.orders.filter { it.status == "Delivered" }
            }

            if (viewModel.isLoading && viewModel.orders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredOrders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders found in this section", color = Color.Gray)
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp)) {
                    items(filteredOrders) { order ->
                        AdminOrderCard(
                            order = order,
                            onUpdateStatus = { newStatus ->
                                viewModel.updateOrderStatus(order.orderId, newStatus)
                            },
                            onVerifyOtp = { otp, onSuccess, onError ->
                                viewModel.verifyDeliveryOtp(order.orderId, otp, onSuccess, onError)
                            },
                            onRequestOtp = {
                                viewModel.requestOtp(order.orderId, order.phone)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(
    order: Order,
    onUpdateStatus: (String) -> Unit,
    onVerifyOtp: (String, () -> Unit, (String) -> Unit) -> Unit,
    onRequestOtp: () -> Unit
) {
    val context = LocalContext.current
    var showOtpDialog by remember { mutableStateOf(false) }
    var otpValue by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }

    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = { Text("Verify Delivery OTP") },
            text = {
                Column {
                    Text("Enter the 4-digit OTP from user's phone", fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = otpValue,
                        onValueChange = { if (it.length <= 4) otpValue = it },
                        label = { Text("OTP") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = otpError != null,
                        supportingText = { otpError?.let { Text(it, color = Color.Red) } }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onVerifyOtp(otpValue, {
                        showOtpDialog = false
                        otpValue = ""
                        otpError = null
                    }, {
                        otpError = it
                    })
                }) { Text("Verify") }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order ID: ${order.orderId.takeLast(8)}", fontWeight = FontWeight.Bold)
                Text("₹${order.totalAmount}", color = Color(0xFF2E7D32), fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(Modifier.height(8.dp))
            Text("User: ${order.userName}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Phone: ${order.phone}", fontSize = 14.sp, color = Color.Gray)
                if (order.phone.isNotEmpty()) {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.phone}"))
                        context.startActivity(intent)
                    }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            Text("Address: ${order.address}", fontSize = 12.sp, color = Color.DarkGray, maxLines = 2)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
            
            Text("Items:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
            order.items.forEach { item ->
                Text("• ${item.name} x${item.quantity}", fontSize = 13.sp)
            }

            Spacer(Modifier.height(16.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Method: ${order.paymentMethod}",
                    fontSize = 12.sp,
                    color = if (order.paymentMethod == "COD") Color.Red else Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    if (order.status == "Pending") {
                        Button(
                            onClick = { onUpdateStatus("Accepted") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("Accept Order", color = Color.White)
                        }
                    } else if (order.status == "Accepted" || order.status == "Incoming") {
                        OutlinedButton(
                            onClick = { onRequestOtp() },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Send OTP")
                        }
                        Button(
                            onClick = { showOtpDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Verify & Deliver", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
