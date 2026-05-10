package com.abhiumale.kartikmartapp.ui.presentation.order_tracking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun OrderTrackingScreen(
    orderId: String,          // Parameter 1
    eta: String = "30 mins",  // Parameter 2 (Default value ke sath)
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {

    Column(modifier = Modifier.fillMaxSize()) {
        Text("OrderTrackingScreen")
    }


}