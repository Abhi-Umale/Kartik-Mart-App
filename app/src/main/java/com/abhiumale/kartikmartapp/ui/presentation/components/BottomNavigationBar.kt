package com.abhiumale.kartikmartapp.ui.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.R
import com.abhiumale.kartikmartapp.domain.model.navItem
import com.abhiumale.kartikmartapp.ui.navigation.Routs

@Composable
fun BottomNavigationBar(navController: NavController,routs: String) {

    //Nav Items
    val navItems = listOf(
        navItem("Home", com.abhiumale.kartikmartapp.R.drawable.regular_outline_home, Routs.HomeRouts),
        navItem("Cart", R.drawable.regular_outline_bag, Routs.CartRouts),
        navItem("Favourites", R.drawable.tracking, Routs.OrdersRouts),
        navItem("Profile", R.drawable.outline_account_circle_24, Routs.ProfileRouts)
    )

    NavigationBar (
        containerColor = MaterialTheme.colorScheme.surface.copy(0.9f),
        contentColor = MaterialTheme.colorScheme.surface,
    ){

        navItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.height(30.dp),
                        tint = if (item.title == routs) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                label = {
                    Text(item.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.size(30.dp),
                onClick = {
                    navController.navigate(item.route){
                        popUpTo (navController.graph.startDestinationId){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selected = item.title == routs,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.secondary.copy(0.04f)
                )
            )

        }
    }
}

