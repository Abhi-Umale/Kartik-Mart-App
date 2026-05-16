package com.abhiumale.kartikmartapp.ui.presentation.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
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

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    orderId: String,
    amount: Double,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val context = LocalContext.current as Activity
    var selectedMethod by remember { mutableStateOf("RAZORPAY") }
    val userMap by viewModel.userData

    val realEmail = userMap?.get("email")?.toString() ?: ""
    val realPhone = userMap?.get("phone")?.toString() ?: ""

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

            // QR Scanner (Simplified for now)
            PaymentOptionTile(
                title = "QR Scanner",
                subtitle = "Scan and Pay directly",
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
                        viewModel.placeOrder(orderId, amount, "QR_SCAN") {
                            navController.navigate(Routs.OrderConfirmationRouts(orderId))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val userName = userMap?.get("name")?.toString() ?: "A User"
                    when (selectedMethod) {
                        "RAZORPAY" -> {
                            startRazorpayPayment(context, amount, orderId, realEmail, realPhone)
                        }
                        "UPI_DIRECT" -> {
                            val upiId = "abhiumale@okaxis" 
                            val name = "Kartik Mart"
                            val uri = Uri.parse("upi://pay?pa=$upiId&pn=$name&am=$amount&cu=INR&tn=Order_$orderId")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            val chooser = Intent.createChooser(intent, "Pay with")
                            try {
                                context.startActivity(chooser)
                                viewModel.placeOrder(orderId, amount, "UPI_DIRECT") {
                                    navController.navigate(Routs.OrderConfirmationRouts(orderId))
                                }
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "No UPI app found", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                        "QR_SCAN" -> {
                            showQRScanner = true
                        }
                        "COD" -> {
                            viewModel.placeOrder(orderId, amount, "COD") {
                                // 1. User ko Notification dikhao
                                showOrderNotification(context, orderId)
                                // 2. Admin ke liye Firebase mein data dalo
                                viewModel.notifyAdmin(orderId, amount, userName)
                                // 3. Navigate karo
                                navController.navigate(Routs.OrderConfirmationRouts(orderId)) {
                                    popUpTo(Routs.PaymentRouts(orderId, amount)) { inclusive = true }
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
