package com.abhiumale.kartikmartapp.ui.presentation.all_product_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.ui.presentation.components.ProductCard
import com.abhiumale.kartikmartapp.ui.presentation.homescreen.HomeViewModel

@Composable
fun TopDealsScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val products = viewModel.productList

    Column {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(10.dp)
        ) {

            items(products) {

                ProductCard(
                    product = it,
                    navController = navController,
                    modifier = Modifier
                )
            }
        }
    }
}