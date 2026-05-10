package com.abhiumale.kartikmartapp.ui.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.ui.navigation.Routs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    navController: NavController,
    onMenuClick: () -> Unit
) {

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(bottom = 10.dp),
        title = {
            TextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search for products") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(26.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(0.8f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedPlaceholderColor = Color.Gray,
                    cursorColor = MaterialTheme.colorScheme.onSurface
                ),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    autoCorrect = true
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchChange(searchQuery)
                    }
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        actions = {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notification",
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(30.dp).padding(end = 4.dp)
            )

            Spacer(Modifier.width(8.dp))

            Icon(imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(30.dp).padding(end = 4.dp)
                    .clickable{
                        navController.navigate(Routs.CartRouts) {
                            launchSingleTop = true
                        }
                    }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.surface
        )
    )
}