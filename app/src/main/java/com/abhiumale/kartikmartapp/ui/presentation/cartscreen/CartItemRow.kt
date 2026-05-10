package com.abhiumale.kartikmartapp.ui.presentation.cartscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.abhiumale.kartikmartapp.data.local.entity.CartEntity

@Composable
fun CartItemRow(
    item: CartEntity,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemoveProduct: () -> Unit
) {

    // Product level calculation
    val mrp = item.price + 20 // Consistent with screen logic
    val totalItemSave = (mrp - item.price) * item.quantity
    val totalItemPay = item.price * item.quantity

    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 1.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Image and Delete Icon Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = item.image,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Image ke niche wala Delete icon
                IconButton(
                    onClick = { onRemoveProduct() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Item",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // CENTER: Details Column
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Unit details", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(Modifier.height(4.dp))

                // Dynamic Price calculation
                val totalItemPay = item.price * item.quantity
                val totalItemSave = (mrp - item.price) * item.quantity

                Text(text = "You Pay ₹${totalItemPay.toInt()}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)

                // Dynamic Save calculation: Ab count ke sath ye bhi badhega
                if (totalItemSave > 0) {
                    Text(text = "You Save ₹${totalItemSave.toInt()}", color = Color(0xFFE65100), style = MaterialTheme.typography.labelSmall)
                }
            }

            // RIGHT: Stepper
            Row(
                modifier = Modifier
                    .width(110.dp)
                    .height(38.dp)
                    .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF2E7D32)).clickable { onDecrease() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.KeyboardArrowDown,
                        contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)
                    )
                }
                Text(item.quantity.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF2E7D32)).clickable { onIncrease() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray) }
}