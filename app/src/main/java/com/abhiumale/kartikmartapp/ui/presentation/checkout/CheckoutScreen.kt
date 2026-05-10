package com.abhiumale.kartikmartapp.ui.presentation.checkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.abhiumale.kartikmartapp.ui.presentation.components.MapAddressPicker
import com.abhiumale.kartikmartapp.ui.presentation.viewmodel.CheckoutViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckoutScreen(
    onNavigateToPayment: (String, Double) -> Unit, // Updated for payment
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val checkoutState by viewModel.uiState.collectAsState()
    var showMap by remember { mutableStateOf(false) }

    // Permission handling
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            viewModel.fetchUserLocation(context)
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    val orderId = "KM${System.currentTimeMillis()}"
                    onNavigateToPayment(orderId, checkoutState.totalPay.toDouble())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 10.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                shape = RoundedCornerShape(16.dp),
                enabled = !checkoutState.isLoading && checkoutState.products.isNotEmpty()
            ) {
                Text(
                    "Continue to Payment",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            CheckoutStepper(currentStep = 2)

            SavingsHeader(
                total = checkoutState.totalPay,
                savings = checkoutState.savings
            )

            DeliveryModeSection(
                isHomeDelivery = checkoutState.isHomeDelivery,
                onModeSelected = { 
                    viewModel.updateDeliveryMode(it)
                    if (it && checkoutState.userAddress.contains("Fetching")) {
                         viewModel.fetchUserLocation(context)
                    }
                }
            )

            if (showMap) {
                MapAddressPicker(
                    onLocationSelected = { _, latLng ->
                        viewModel.updateAddressFromLocation(context, latLng)
                        showMap = false
                    },
                    onDismiss = { showMap = false }
                )
            }
            
            AddressSummaryCard(
                title = if (checkoutState.isHomeDelivery) "Home Delivery" else "Pick Up Point (Amravati Office)",
                address = checkoutState.userAddress,
                onEditClick = {
                    if (checkoutState.isHomeDelivery) {
                        showMap = true
                    } else {
                        viewModel.updateAddress("Kartik Mart HQ, Nagpur Road, Amravati")
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (checkoutState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
            }

            if (checkoutState.products.isNotEmpty()) {
                OrderItemsList(products = checkoutState.products)
            } else if (!checkoutState.isLoading) {
                Text("No items to checkout", modifier = Modifier.padding(16.dp), color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            PriceSummaryCard(
                mrp = checkoutState.mrp.toInt(),
                savings = checkoutState.savings,
                delivery = checkoutState.deliveryFee
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}