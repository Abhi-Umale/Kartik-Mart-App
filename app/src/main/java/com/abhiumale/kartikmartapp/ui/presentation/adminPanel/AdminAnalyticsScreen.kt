package com.abhiumale.kartikmartapp.ui.presentation.adminPanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnalyticsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text("Sales Overview", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(24.dp))
            
            // Simple Bar Chart
            Card(
                modifier = Modifier.fillMaxWidth().height(250.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val data = listOf(
                        "Mon" to 0.4f,
                        "Tue" to 0.7f,
                        "Wed" to 0.5f,
                        "Thu" to 0.9f,
                        "Fri" to 0.6f,
                        "Sat" to 0.8f,
                        "Sun" to 1.0f
                    )
                    
                    data.forEach { (day, value) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                Modifier
                                    .width(30.dp)
                                    .fillMaxHeight(value)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(day, fontSize = 10.sp)
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))
            Text("Revenue Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AnalyticsStatCard("Weekly", "₹12,450", Color(0xFF4CAF50), Modifier.weight(1f))
                AnalyticsStatCard("Monthly", "₹54,200", Color(0xFF2196F3), Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AnalyticsStatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontSize = 14.sp, color = color)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
