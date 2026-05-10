package com.abhiumale.kartikmartapp.ui.presentation.adminPanel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Users") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (viewModel.isLoading && viewModel.users.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(viewModel.users) { user ->
                    val name = user["name"] as? String ?: "No Name"
                    val email = user["email"] as? String ?: "No Email"
                    val role = user["role"] as? String ?: "USER"
                    val uid = user["uid"] as? String ?: ""
                    val isBlocked = user["isBlocked"] as? Boolean ?: false

                    ListItem(
                        headlineContent = { Text(name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("$email • $role") },
                        trailingContent = {
                            Button(
                                onClick = { viewModel.toggleUserBlock(uid, isBlocked) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isBlocked) Color.Green else Color.Red
                                )
                            ) {
                                Icon(
                                    if (isBlocked) Icons.Default.CheckCircle else Icons.Default.Block,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(if (isBlocked) "Unblock" else "Block")
                            }
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                }
            }
        }
    }
}
