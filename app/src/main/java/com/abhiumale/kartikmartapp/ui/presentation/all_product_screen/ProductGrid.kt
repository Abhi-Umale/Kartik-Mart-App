package com.abhiumale.kartikmartapp.ui.presentation.all_product_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.domain.model.Product
import com.abhiumale.kartikmartapp.ui.presentation.components.ProductCard

@Composable
fun ProductGrid(
    products: List<Product>,
    navController: NavController
) {

    Column(modifier = Modifier.fillMaxWidth() ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 2 items per row
        val rows = products.chunked(2)
        rows.forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                rowItems.forEach { product ->
                    ProductCard(
                        product = product,
                        navController = navController,
                        modifier = Modifier
                            .weight(1f)
                            .padding(6.dp),
                        showDetails = true
                    )
                }
                // agar odd items ho to empty space fill kare
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}