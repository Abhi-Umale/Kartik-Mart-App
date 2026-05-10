package com.abhiumale.kartikmartapp.ui.presentation.productdetailscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.abhiumale.kartikmartapp.data.local.entity.CartEntity
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.abhiumale.kartikmartapp.ui.presentation.cartscreen.CartViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: Int,
    name: String,
    price: Double,
    mrp: Double,
    image: String,
    brand: String = "",
    category: String = "",
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    var selectedTab by remember { mutableStateOf("Description") }
    val cartList = cartViewModel.cartItems
    val cartItem = cartList.find { it.id == productId }
    val quantity = cartItem?.quantity ?: 0

    val discount = mrp - price
    val colors = MaterialTheme.colorScheme

    val decodedName = try {
        URLDecoder.decode(name, StandardCharsets.UTF_8.toString())
    } catch (e: Exception) {
        name
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            tint = colors.primary,
                            modifier = Modifier.size(25.dp),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Routs.CartRouts) {
                                launchSingleTop = true
                            }
                        },
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            tint = colors.primary,
                            modifier = Modifier.size(25.dp),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            navController.navigate(Routs.ProfileRouts) {
                                launchSingleTop = true
                            }
                        },
                    ) {
                        Icon(
                            Icons.Default.Person,
                            tint = colors.primary,
                            modifier = Modifier.size(25.dp),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Sticky Add to Cart Button
            Surface(shadowElevation = 12.dp, color = Color.White) {
                if (quantity == 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                cartViewModel.addToCart(
                                    CartEntity(productId, name, price, mrp, image)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("ADD TO CART", fontWeight = FontWeight.ExtraBold)
                        }

                        Button(
                            onClick = {
                                navController.navigate(Routs.CheckoutScreen(productId = productId.toString())){
                                    launchSingleTop = true
                                    popUpTo<Routs.CheckoutScreen> {
                                        inclusive = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Buy at $price", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {

                        val totalPrice = price * quantity
                        val totalMrp = mrp * quantity
                        val totalSave = totalMrp - totalPrice

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // --- LEFT SIDE: Price Details ---
                            Column {

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "You Pay ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        text = "₹${totalPrice.toInt()}.00",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }

                                if (totalSave > 0) {
                                    Text(
                                        text = "You Save ₹${totalSave.toInt()}.00",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFE65100), // Orange color for savings
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // --- RIGHT SIDE: Stepper (Screenshot style) ---
                            Row(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(44.dp)
                                    .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Minus / Delete
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                        .background(Color(0xFF2E7D32))
                                        .clickable { cartViewModel.removeFromCart(productId) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (quantity == 1) Icons.Default.Delete else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Quantity Text
                                Text(
                                    text = quantity.toString(),
                                    modifier = Modifier.weight(1.2f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                // Plus Button
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                        .background(Color(0xFF2E7D32))
                                        .clickable {
                                            cartViewModel.addToCart(
                                                CartEntity(productId, name, price, mrp, image)
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = {
                                navController.navigate(Routs.CheckoutScreen(productId = productId.toString())) {
                                    launchSingleTop = true
                                    popUpTo<Routs.CheckoutScreen> {
                                        inclusive = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Buy at ₹${totalPrice.toInt()}", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {

            // 1. Image Section with Glass Effect & Floating Image
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    colors.onSurface.copy(0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.1f),
                        contentAlignment = Alignment.Center
                    ) {
                        // Image with Graphics Layer for "Floating" Look
                        AsyncImage(
                            model = image,
                            contentDescription = name,
                            contentScale = ContentScale.Fit, // Image ka ratio nahi bigdega
                            modifier = Modifier
                                .fillMaxSize(0.85f) // Card ke andar thodi jagah chhodne ke liye
                                .graphicsLayer {
                                    // Shadow ko image ke exact shape par dikhane ke liye:
                                    shadowElevation = 500f // Isse image uthi hui dikhegi
                                    shape = RoundedCornerShape(16.dp)
                                    clip = false
                                    scaleX = 1.05f
                                    scaleY = 1.05f
                                }
                        )

                        // Veg Icon (Top Left)
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp),
                            border = BorderStroke(1.5.dp, colors.primary),
                            shape = RoundedCornerShape(4.dp),
                            color = colors.background
                        ) {
                            Box(
                                Modifier
                                    .padding(4.dp)
                                    .size(10.dp)
                                    .background(colors.primary, CircleShape)
                            )
                        }
                    }
                }
            }
            // 2. Product Title & Details
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = category.uppercase(),
                        color = colors.onSurface,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.Underline
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = decodedName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price Section
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "₹${price.toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2E7D32) // Green color for price
                        )

                        Spacer(Modifier.width(8.dp))

                        // Percentage Calculation
                        val discountPercentage = if (mrp > 0) {
                            ((mrp - price) / mrp * 100).toInt()
                        } else 0

                        if (discountPercentage > 0) {
                            Surface(
                                color = Color(0xFFFFE0B2),
                                shape = RoundedCornerShape(6.dp),
                            ) {
                                Text(
                                    text = "$discountPercentage% OFF", // Ab ye "15% OFF" dikhayega
                                    color = Color(0xFFE65100),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "MRP ₹${mrp.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            textDecoration = TextDecoration.LineThrough,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "(MRP inclusive of all taxes)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.height(18.dp))

                    // Volume Section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = colors.primary)
                        ) {
                            Text(
                                "4 U x 250 ml",
                                modifier = Modifier.padding(8.dp),
                                color = Color.White
                            )
                        }

                        if (quantity > 0) {
                            Text(
                                text = " x $quantity selected",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(start = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }
            }
            // 3. Tabs (Description, Disclaimer, etc)
            item {
                val tabs = listOf("Description", "Disclaimer", "More Info")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEach { tab ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedTab = tab }
                        ) {
                            Text(
                                text = tab,
                                color = if (selectedTab == tab) Color(0xFF2E7D32) else Color.Gray,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                            if (selectedTab == tab) {
                                Box(
                                    Modifier
                                        .height(2.dp)
                                        .width(60.dp)
                                        .background(Color(0xFF2E7D32))
                                )
                            }
                        }
                    }
                }
                val contentText = when (selectedTab) {
                    "Description" -> "Easy mixing ratios for consistent flavour every time..."
                    "Disclaimer" -> "While we work to ensure that product information is correct..."
                    "More Info" -> "Manufacturer: Malgudi Trading Company\nPacker: SL Veggies..."
                    else -> ""
                }

                Text(
                    text = contentText,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}