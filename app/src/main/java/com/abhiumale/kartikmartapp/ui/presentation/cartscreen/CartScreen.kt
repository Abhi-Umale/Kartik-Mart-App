package com.abhiumale.kartikmartapp.ui.presentation.cartscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val colors = MaterialTheme.colorScheme
    val cartItems = cartViewModel.cartItems
    val totalPay = cartItems.sumOf { it.price * it.quantity }
    val totalSavings = cartItems.sumOf { item ->
        (item.mrp - item.price) * item.quantity
    }

    if (cartItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
                Text(
                    "Your cart is empty!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Gray
                )
                Button(
                    onClick = { navController.navigate(Routs.HomeRouts) },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Go back to Shop")
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(25.dp),
                            )
                        }
                    },
                    title = { Text("Cart", fontWeight = FontWeight.Bold, color = colors.primary) },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(25.dp),
                            )
                        }
                        IconButton(onClick = { navController.navigate(Routs.ProfileRouts)}) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(25.dp),
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Surface(shadowElevation = 12.dp, color = Color.White) {
                    Button(
                        onClick = {
                            navController.navigate(Routs.CheckoutScreen()){
                                launchSingleTop = true
                                popUpTo(Routs.CartRouts) {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(
                            "PROCEED TO CHECKOUT",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                item {
                    Surface(color = Color.White) {
                        HorizontalDivider(
                            thickness = 3.dp,
                            color = Color.LightGray
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Savings ₹${totalSavings.toInt()}",
                                color = Color(0xFFE65100),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Cart Total ₹${totalPay.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "My Cart",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                        Surface(
                            color = Color(0xFF2E7D32).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${cartItems.size} Item${if (cartItems.size > 1) "s" else ""}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                }
                items(cartItems) { item ->
                    val decodedName = try {
                        URLDecoder.decode(item.name, "UTF-8")
                    } catch (e: Exception) {
                        item.name
                    }

                    CartItemRow(
                        item = item.copy(name = decodedName),
                        onIncrease = { cartViewModel.addToCart(item) },
                        onDecrease = { cartViewModel.removeFromCart(item.id) },
                        onRemoveProduct = { cartViewModel.deleteProduct(item.id) }
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        TextButton(
                            onClick = { cartViewModel.clearCart() },
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                            Text(
                                " Remove all",
                                color = Color.Red,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
