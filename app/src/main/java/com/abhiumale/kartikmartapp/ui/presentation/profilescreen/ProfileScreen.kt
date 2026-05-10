package com.abhiumale.kartikmartapp.ui.presentation.profilescreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.abhiumale.kartikmartapp.ui.presentation.components.BottomNavigationBar
import com.abhiumale.kartikmartapp.ui.presentation.registrationscreens.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
    }

    val userData = viewModel.userData
    val isLoading = viewModel.isLoading
    val name = userData?.get("name")?.toString() ?: "User"
    val phone = userData?.get("phone")?.toString() ?: ""
    val profilePicUrl = userData?.get("profileImage")?.toString()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, "Profile") },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2E7D32))
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // 1. Header Card
                Surface(color = Color(0xFFE8F5E9), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // PROFILE IMAGE BOX
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clickable { launcher.launch("image/*") } // Click to change
                            ) {
                                if (!profilePicUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = profilePicUrl,
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        tint = Color.Gray
                                    )
                                }
                                // Chhota Add Icon
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.BottomEnd).size(20.dp),
                                    tint = Color(0xFF2E7D32)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Hey! $name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        // Savings Card
                        Card(
                            modifier = Modifier.padding(top = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Customers like you save", fontSize = 14.sp, color = Color.Gray)
                                Text("₹22,552 every year", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            }
                        }
                    }
                }

                // 2. Information List
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Information", fontWeight = FontWeight.Bold, color = Color.Gray)
                    ProfileMenuItem(Icons.Default.Notifications, "My Notifications")
                    ProfileMenuItem(Icons.Default.Call, "Phone: $phone")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Help & Support", fontWeight = FontWeight.Bold, color = Color.Gray)
                    ProfileMenuItem(Icons.Default.Info, "Help @ Kartik Mart")
                    ProfileMenuItem(Icons.Default.QuestionAnswer, "FAQs") // Icon fixed here

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            viewModel.logout()
                            navController.navigate(Routs.LoginRouts) { popUpTo(0) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("SIGN OUT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}