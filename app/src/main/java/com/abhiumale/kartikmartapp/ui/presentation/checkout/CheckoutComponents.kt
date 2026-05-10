package com.abhiumale.kartikmartapp.ui.presentation.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.abhiumale.kartikmartapp.domain.model.CartItem
import com.abhiumale.kartikmartapp.domain.model.CheckoutUiState

// 1. Savings Header (Top Bar ke niche wala section)
@Composable
fun SavingsHeader(total: Int, savings: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Savings ₹$savings", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "You Pay ", color = Color.Gray)
            Text(text = "₹$total", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

// 2. Delivery Mode (Pick Up vs Home Delivery)
@Composable
fun DeliveryModeSection(isHomeDelivery: Boolean, onModeSelected: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DeliveryOptionCard(
            title = "Pick Up Point",
            subtitle = "Free Delivery",
            isSelected = !isHomeDelivery,
            modifier = Modifier.weight(1f),
            onClick = { onModeSelected(false) }
        )
        DeliveryOptionCard(
            title = "Home Delivery",
            subtitle = "Flat ₹49",
            isSelected = isHomeDelivery,
            modifier = Modifier.weight(1f),
            onClick = { onModeSelected(true) }
        )
    }
}

@Composable
fun DeliveryOptionCard(title: String, subtitle: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val borderColor = if (isSelected) Color(0xFF2E7D32) else Color.LightGray
    Card(
        modifier = modifier
            .clickable { onClick() }
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

// 3. Address Summary Card (Professional Look)
@Composable
fun AddressSummaryCard(title: String, address: String, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2E7D32))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                Text(text = address, fontSize = 13.sp, color = Color.DarkGray)
            }
            TextButton(onClick = onEditClick) {
                Text("CHANGE", color = Color(0xFF1976D2))
            }
        }
    }
}

// 4. Checkout Stepper (Progress Tracker)
@Composable
fun CheckoutStepper(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val steps = listOf("Address", "Summary", "Payment")
        steps.forEachIndexed { index, label ->
            val stepNum = index + 1
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (stepNum <= currentStep) Color(0xFF2E7D32) else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (stepNum < currentStep) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text("$stepNum", color = Color.White, fontSize = 12.sp)
                    }
                }
                Text(label, fontSize = 11.sp)
            }
        }
    }
}

// 5. Price Summary Card (Screenshot 4 inspired)
@Composable
fun PriceSummaryCard(mrp: Int, savings: Int, delivery: Int) {

    val totalPayable = mrp - savings + delivery // Final Amount logic
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Price Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            PriceRow(label = "Total MRP", value = "₹$mrp")
            PriceRow(label = "Discount", value = "- ₹$savings", valueColor = Color(0xFF2E7D32))
            PriceRow(label = "Delivery Charges", value = if (delivery == 0) "FREE" else "₹$delivery", valueColor = Color(0xFF2E7D32))

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Amount", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "₹$totalPayable", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PriceRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray)
        Text(text = value, color = valueColor, fontWeight = FontWeight.Medium)
    }
}

// CheckoutComponent file mein
@Composable
fun OrderItemsList(products: List<CartItem>) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Items in Order (${products.size})",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            if (isExpanded) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                products.forEach { item ->
                    OrderItemRow(item)
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(45.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Qty: ${item.quantity}", fontSize = 12.sp, color = Color.Gray)
        }
        Text("₹${item.price * item.quantity}", fontWeight = FontWeight.Bold)
    }
}