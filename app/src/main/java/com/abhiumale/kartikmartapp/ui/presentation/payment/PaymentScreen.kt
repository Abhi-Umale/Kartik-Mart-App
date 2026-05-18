package com.abhiumale.kartikmartapp.ui.presentation.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.abhiumale.kartikmartapp.ui.notification.showOrderNotification
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    orderId: String,
    amount: Double,
    address: String,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val context = LocalContext.current as Activity
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var selectedMethod by remember { mutableStateOf("RAZORPAY") }
    val userMap by viewModel.userData

    val realEmail = userMap?.get("email")?.toString() ?: ""
    val realPhone = userMap?.get("phone")?.toString() ?: ""
    val userName = userMap?.get("name")?.toString() ?: "User"

    val upiLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val response = data?.getStringExtra("response") ?: ""
        
        // Check if UPI payment is success
        if (response.lowercase().contains("success")) {
            scope.launch { PaymentResultRegistry.paymentResults.emit(true) }
        } else if (response.lowercase().contains("cancel") || response.isEmpty()) {
            // Some UPI apps don't return "success" but the user might have paid. 
            // For safety in this fix, we assume failure if not explicitly success.
            Toast.makeText(context, "Payment Not Confirmed", Toast.LENGTH_SHORT).show()
            scope.launch { PaymentResultRegistry.paymentResults.emit(false) }
        } else {
            scope.launch { PaymentResultRegistry.paymentResults.emit(false) }
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        PaymentResultRegistry.paymentResults.collect { success ->
            if (success) {
                viewModel.handlePaymentResult(true, orderId, amount)
                viewModel.placeOrder(orderId, amount, selectedMethod, address) {
                    navController.navigate(Routs.OrderConfirmationRouts(orderId)) {
                        popUpTo(Routs.PaymentRouts(orderId, amount, address)) { inclusive = true }
                    }
                }
            } else {
                viewModel.handlePaymentResult(false, orderId, amount, "Payment failed or cancelled")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Price Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Total Payable Amount", color = Color.Gray, fontSize = 14.sp)
                    Text("₹$amount", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // PhonePe / GPay / UPI (Direct)
            PaymentOptionTile(
                title = "UPI (PhonePe, GPay, etc.)",
                subtitle = "Pay directly using your favorite UPI app",
                isSelected = selectedMethod == "UPI_DIRECT",
                icon = Icons.Default.Smartphone,
                onSelect = { selectedMethod = "UPI_DIRECT" }
            )

            // Razorpay
            PaymentOptionTile(
                title = "Online Payment",
                subtitle = "Cards, Wallets & More (via Razorpay)",
                isSelected = selectedMethod == "RAZORPAY",
                icon = Icons.Default.AccountBalanceWallet,
                onSelect = { selectedMethod = "RAZORPAY" }
            )

            // QR Scanner
            PaymentOptionTile(
                title = "QR Scanner",
                subtitle = "Scan Admin QR and Pay",
                isSelected = selectedMethod == "QR_SCAN",
                icon = Icons.Default.QrCodeScanner,
                onSelect = { selectedMethod = "QR_SCAN" }
            )

            // COD
            PaymentOptionTile(
                title = "Cash on Delivery",
                subtitle = "Pay when you receive your order",
                isSelected = selectedMethod == "COD",
                icon = Icons.Default.Payments,
                onSelect = { selectedMethod = "COD" }
            )

            var showQRScanner by remember { mutableStateOf(false) }
            if (showQRScanner) {
                com.abhiumale.kartikmartapp.ui.presentation.components.QRScannerDialog(
                    onDismiss = { showQRScanner = false },
                    onCodeScanned = { code ->
                        showQRScanner = false
                        if (code.contains("upi://pay")) {
                             val intent = Intent(Intent.ACTION_VIEW, Uri.parse(code))
                             upiLauncher.launch(intent)
                        } else {
                            Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when (selectedMethod) {
                        "RAZORPAY" -> {
                            startRazorpayPayment(context, amount, orderId, realEmail, realPhone)
                            // Note: Confirmation navigation happens in MainActivity on success
                        }
                        "UPI_DIRECT" -> {
                            val upiId = "abhiumale@okaxis" 
                            val name = "Kartik Mart Admin"
                            val uri = Uri.parse("upi://pay?pa=$upiId&pn=$name&am=$amount&cu=INR&tn=Order_$orderId")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            val chooser = Intent.createChooser(intent, "Pay with")
                            try {
                                upiLauncher.launch(chooser)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No UPI app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                        "QR_SCAN" -> {
                            showQRScanner = true
                        }
                        "COD" -> {
                            viewModel.placeOrder(orderId, amount, "COD", address) {
                                navController.navigate(Routs.OrderConfirmationRouts(orderId)) {
                                    popUpTo(Routs.PaymentRouts(orderId, amount, address)) { inclusive = true }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (selectedMethod == "COD") "Place Order" else "Proceed to Pay",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun PaymentOptionTile(title: String, subtitle: String, isSelected: Boolean, icon: ImageVector, onSelect: () -> Unit) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, if (isSelected) Color(0xFFFFD700) else Color.Transparent),
        color = if (isSelected) Color(0xFFFFFDE7) else Color(0xFFF9F9F9),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color.Black else Color.Gray, modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
