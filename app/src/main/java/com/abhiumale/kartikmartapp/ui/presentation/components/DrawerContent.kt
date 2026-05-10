package com.abhiumale.kartikmartapp.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhiumale.kartikmartapp.ui.navigation.Routs

@Composable
fun DrawerContent(
    userName: String,
    onDestinationClicked: (Routs) -> Unit,
    currentLocation: String = "Amravati, Maharashtra"
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(24.dp)
        ) {
            Text(
                text = "Hi, $userName",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation Items
        NavigationDrawerItem(
            label = { Text("My Cart") },
            selected = false,
            onClick = { onDestinationClicked(Routs.CartRouts) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) }
        )

        NavigationDrawerItem(
            label = { Text("Shop By Category") },
            selected = false,
            onClick = { /* Handle category logic */ },
            icon = { Icon(Icons.Default.Menu, contentDescription = null) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Custom Options
        DrawerMenuItem(label = "My Orders") { onDestinationClicked(Routs.OrdersRouts) }
        DrawerMenuItem(label = "About Us") { /* Navigate to About */ }

        Spacer(modifier = Modifier.weight(1f)) // Push content to bottom

        // Location & Version Section
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(text = currentLocation, color = Color.Gray, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Version 1.0.0",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun DrawerMenuItem(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        style = TextStyle(fontSize = 16.sp)
    )
}
