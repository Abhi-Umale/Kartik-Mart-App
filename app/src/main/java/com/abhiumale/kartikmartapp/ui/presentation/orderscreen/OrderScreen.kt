package com.abhiumale.kartikmartapp.ui.presentation.orderscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.abhiumale.kartikmartapp.domain.model.CartItem
import com.abhiumale.kartikmartapp.domain.model.Order
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navController: NavController,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val orders by viewModel.orders
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    val currentOrders = orders.filter { it.status.lowercase() != "delivered" && it.status.lowercase() != "cancelled" }
    val historyOrders = orders.filter { it.status.lowercase() == "delivered" || it.status.lowercase() == "cancelled" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FB)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading && orders.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFFFD700)
                )
            } else if (errorMessage != null && orders.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = errorMessage ?: "Unknown error", color = Color.Red)
                    Button(onClick = { viewModel.fetchOrders() }) {
                        Text("Retry")
                    }
                }
            } else if (orders.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text(text = "No orders yet", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    if (currentOrders.isNotEmpty()) {
                        item {
                            Text(
                                "Ongoing Orders",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                            )
                        }
                        items(currentOrders) { order ->
                            OngoingOrderCard(order, navController)
                        }
                    }

                    if (historyOrders.isNotEmpty()) {
                        item {
                            Text(
                                "Order History",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp, start = 4.dp, end = 4.dp)
                            )
                        }
                        items(historyOrders) { order ->
                            HistoryOrderCard(order, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OngoingOrderCard(order: Order, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(8.dp).background(Color(0xFF2196F3), CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = order.status,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.weight(1f))
                Text("₹${order.totalAmount}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            order.items.forEach { item ->
                OrderItemRow(item, navController)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            if (order.status == "Accepted" || order.status == "Incoming") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .background(Color(0xFFFFF9C4), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Delivery OTP: ${order.deliveryOtp ?: "----"}",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Order ID: ${order.orderId.takeLast(8)}", fontSize = 12.sp, color = Color.Gray)
                    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                    Text("Placed: ${sdf.format(Date(order.timestamp))}", fontSize = 12.sp, color = Color.Gray)
                }
                
                Button(
                    onClick = {
                        navController.navigate(Routs.OrderTracking(order.orderId))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Track Order", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun HistoryOrderCard(order: Order, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusColor = if (order.status.lowercase() == "delivered") Color(0xFF4CAF50) else Color.Red
                Text(
                    text = order.status,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.weight(1f))
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                Text(sdf.format(Date(order.timestamp)), fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(8.dp))

            order.items.forEach { item ->
                OrderItemRow(item, navController, isHistory = true)
            }

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("Total: ₹${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun OrderItemRow(item: CartItem, navController: NavController, isHistory: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                val encodedUrl = java.net.URLEncoder.encode(item.imageUrl, "UTF-8")
                navController.navigate(
                    Routs.ProductDetailRouts(
                        productId = item.productId.toLongOrNull() ?: 0L,
                        name = item.name,
                        price = item.price.toDouble(),
                        mrp = item.mrp.toDouble(),
                        image = encodedUrl,
                        category = item.category
                    )
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(if (isHistory) 40.dp else 50.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = "Qty: ${item.quantity}", fontSize = 12.sp, color = Color.Gray)
        }
        if (!isHistory) {
            Text(text = "₹${item.price}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
