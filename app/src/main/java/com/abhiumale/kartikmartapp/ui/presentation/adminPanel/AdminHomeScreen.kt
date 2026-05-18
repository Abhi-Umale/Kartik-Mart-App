package com.abhiumale.kartikmartapp.ui.presentation.adminPanel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.components.AdminActionCard
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.components.AdminStatCard
import com.abhiumale.kartikmartapp.ui.presentation.registrationscreens.AuthViewModel

data class AdminOption(val title: String, val icon: ImageVector, val color: Color, val route: Routs)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    adminViewModel: AdminViewModel = hiltViewModel()
) {
    val options = listOf(
        AdminOption("Users", Icons.Default.People, Color(0xFF2196F3), Routs.AdminUsersRouts),
        AdminOption("Products", Icons.Default.Inventory, Color(0xFF4CAF50), Routs.AdminProductsRouts),
        AdminOption("Orders", Icons.Default.ShoppingCart, Color(0xFFFF9800), Routs.AdminOrdersRouts),
        AdminOption("Analytics", Icons.Default.BarChart, Color(0xFF9C27B0), Routs.AdminAnalyticsRouts),
        AdminOption("Categories", Icons.Default.Category, Color(0xFFE91E63), Routs.AdminCategoriesRouts),
        AdminOption("Brands", Icons.Default.BrandingWatermark, Color(0xFF795548), Routs.AdminBrandsRouts),
        AdminOption("Coupons", Icons.Default.ConfirmationNumber, Color(0xFF00BCD4), Routs.AdminCouponsRouts),
        AdminOption("Notifications", Icons.Default.Notifications, Color(0xFFFFC107), Routs.AdminNotificationRouts),
        AdminOption("Settings", Icons.Default.Settings, Color(0xFF607D8B), Routs.AdminSettingsRouts),
        AdminOption("Profile", Icons.Default.Person, Color(0xFF673AB7), Routs.AdminProfileRouts)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kartik Mart Admin", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate(Routs.AdminNotificationRouts) }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Routs.LoginRouts) { popUpTo(0) }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routs.AddProductRouts) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Dashboard Overview", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminStatCard(
                    "Total Products",
                    adminViewModel.productCount.toString(),
                    Color(0xFF314C33),
                    Modifier.weight(1f)
                )
                AdminStatCard(
                    "Total Users",
                    adminViewModel.userCount.toString(),
                    Color(0xFF3C6D95),
                    Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(options) { option ->
                    AdminActionCard(option.title, option.icon, option.color) {
                        navController.navigate(option.route)
                    }
                }
            }
        }
    }
}
