package com.abhiumale.kartikmartapp.ui.presentation.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.domain.model.Product
import com.abhiumale.kartikmartapp.ui.presentation.components.ProductCard

@Composable
fun ProductHorizontalList(
    products: List<Product>,
    navController: NavController
) {

    //  Step 1: group by brand
    val uniqueBrandProducts = products
        .groupBy { it.brand }
        .map { (_, productList) ->
            productList.maxByOrNull { it.rating }!!   // highest rating per brand
        }

    //  Step 2: top 10 by rating
    val topProducts = uniqueBrandProducts
        .sortedByDescending { it.rating }
        .take(10)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(topProducts) {
            ProductCard(
                product = it,
                navController = navController,
                modifier = Modifier
            )
        }
    }
}
