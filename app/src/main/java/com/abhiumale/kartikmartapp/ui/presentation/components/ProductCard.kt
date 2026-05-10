package com.abhiumale.kartikmartapp.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.abhiumale.kartikmartapp.data.local.entity.CartEntity
import com.abhiumale.kartikmartapp.domain.model.Product
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.abhiumale.kartikmartapp.ui.presentation.cartscreen.CartViewModel
import java.net.URLEncoder

@Composable
fun ProductCard(
    product: Product,
    navController: NavController,
    modifier: Modifier = Modifier,
    showDetails: Boolean = false,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val colors = MaterialTheme.colorScheme

    // Sync state with CartViewModel
    val cartList = cartViewModel.cartItems
    val cartItem = cartList.find { it.id.toLong() == product.id }
    val quantity = cartItem?.quantity ?: 0

    Card(
        modifier = modifier
            .padding(6.dp)
            .width(160.dp)
            .height(if (showDetails) 280.dp else 235.dp)
            .clickable {
                val encodedImage = URLEncoder.encode(product.image, "UTF-8")
                val encodedName = URLEncoder.encode(product.name, "UTF-8")
                navController.navigate(
                    Routs.ProductDetailRouts(product.id, encodedName, product.price, product.mrp, encodedImage, product.category)
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxSize()
        ) {
            // 1. Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp) // Thoda sa kam kiya details accommodate karne ke liye
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.onSurface.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.image)
                        .crossfade(true).build(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Brand/Category (Sirf Grid mein dikhega)
            if (showDetails) {
                Text(
                    text = product.brand.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.secondary,
                    fontWeight = FontWeight.Bold
                )
            }

            // 2. Product Name
            Text(
                text = product.name,
                color = colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall
            )

            // 3. Weight aur Rating Row (Sirf Grid mein dikhega)
            if (showDetails) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.weight,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "⭐ ${product.rating}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFFFB300), // Gold color
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 4. Price
            Text(
                text = "₹${product.price}",
                color = colors.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            if (showDetails){
                Spacer(modifier = Modifier.height(12.dp))
            }else{
                Spacer(modifier = Modifier.weight(1f))
            }
            // 5. Add Button
            if (quantity == 0) {
                Button(
                    onClick = {
                        cartViewModel.addToCart(
                            CartEntity(
                                id = product.id.toInt(),
                                name = product.name,
                                price = product.price,
                                mrp = product.mrp,
                                image = product.image
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("ADD", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            } else {
                // Stepper (Right side wala design only)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minus
                    Box(
                        modifier = Modifier.fillMaxHeight().weight(1f).background(Color(0xFF2E7D32))
                            .clickable { cartViewModel.removeFromCart(product.id.toInt()) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (quantity == 1) Icons.Default.Delete else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Quantity
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    // Plus
                    Box(
                        modifier = Modifier.fillMaxHeight().weight(1f).background(Color(0xFF2E7D32))
                            .clickable {
                                cartViewModel.addToCart(
                                    CartEntity(
                                        id = product.id.toInt(),
                                        name = product.name,
                                        price = product.price,
                                        mrp = product.mrp,
                                        image = product.image
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
