package com.abhiumale.kartikmartapp.ui.presentation.homescreen

import android.R.attr.category
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.abhiumale.kartikmartapp.R
import com.abhiumale.kartikmartapp.domain.model.Product
import com.abhiumale.kartikmartapp.ui.navigation.Routs

@Composable
fun CategoryRow(selectedCategory: String, onCategorySelected: (String) -> Unit) {

    val categories = listOf(
        Pair("All", painterResource(R.drawable.everything)),
        Pair("Biscuits", painterResource(R.drawable.milk)),
        Pair("Snacks", painterResource(R.drawable.oil)),
        Pair("Art Supplies", painterResource(R.drawable.snacks)),
        Pair("Baby Foods", painterResource(R.drawable.chocklate)),
        Pair("Masala & Spices", painterResource(R.drawable.drinks)),
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(vertical = 8.dp)
    ) {

        items(categories) { (name, image) ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable {
                        onCategorySelected(name.lowercase())
                    }
            ) {

                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(0.4f)),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = image,
                        contentDescription = name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = name,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (selectedCategory == name) {

                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(25.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }
    }
}