package com.abhiumale.kartikmartapp.ui.presentation.adminPanel

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(navController: NavController) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var maintenanceMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            ListItem(
                headlineContent = { Text("Enable Notifications") },
                trailingContent = { Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it }) }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Maintenance Mode") },
                supportingContent = { Text("Disable app for all users") },
                trailingContent = { Switch(checked = maintenanceMode, onCheckedChange = { maintenanceMode = it }) }
            )
            HorizontalDivider()
            Spacer(Modifier.height(32.dp))
            Button(onClick = { /* Save Settings */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Changes")
            }
        }
    }
}
