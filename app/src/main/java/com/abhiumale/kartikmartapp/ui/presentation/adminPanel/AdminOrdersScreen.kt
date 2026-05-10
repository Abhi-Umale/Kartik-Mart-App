package com.abhiumale.kartikmartapp.ui.presentation.adminPanel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending", "Incoming", "Delivered")

    LaunchedEffect(Unit) {
        viewModel.fetchOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            val filteredOrders = when (selectedTab) {
                0 -> viewModel.orders.filter { it.status == "Pending" }
                1 -> viewModel.orders.filter { it.status == "Incoming" }
                else -> viewModel.orders.filter { it.status == "Delivered" }
            }

            if (viewModel.isLoading && viewModel.orders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredOrders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders found", color = Color.Gray)
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(filteredOrders) { order ->
                        OrderListItem(order, onUpdateStatus = { newStatus ->
                            viewModel.updateOrderStatus(order.orderId, newStatus)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun OrderListItem(order: com.abhiumale.kartikmartapp.domain.model.Order, onUpdateStatus: (String) -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order ID: ${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold)
                Text("₹${order.totalAmount}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text("User: ${order.userName}", fontSize = 14.sp)
            Text("Status: ${order.status}", color = getStatusColor(order.status), fontWeight = FontWeight.Medium)
            
            Spacer(Modifier.height(8.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (order.status != "Delivered") {
                    TextButton(onClick = { onUpdateStatus("Delivered") }) {
                        Text("Mark Delivered")
                    }
                }
                if (order.status == "Pending") {
                    TextButton(onClick = { onUpdateStatus("Incoming") }) {
                        Text("Accept Order")
                    }
                }
            }
        }
    }
}

fun getStatusColor(status: String): Color {
    return when (status) {
        "Pending" -> Color(0xFFFF9800)
        "Incoming" -> Color(0xFF2196F3)
        "Delivered" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }
}
