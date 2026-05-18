package com.abhiumale.kartikmartapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.abhiumale.kartikmartapp.ui.navigation.NavGraph
import com.abhiumale.kartikmartapp.ui.notification.NotificationUtils
import com.abhiumale.kartikmartapp.ui.theme.KartikMartAppTheme
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultListener {

    // Ideally, pass the result to a shared ViewModel or trigger notifications here
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Checkout.preload(applicationContext) // Preload Razorpay
        enableEdgeToEdge()
        setContent {
            KartikMartAppTheme {
               NavGraph()
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        kotlinx.coroutines.MainScope().launch {
            com.abhiumale.kartikmartapp.ui.presentation.payment.PaymentResultRegistry.paymentResults.emit(true)
        }
        NotificationUtils.showNotification(
            this, 
            "Payment Successful! ✅", 
            "Your payment has been processed. ID: $razorpayPaymentId"
        )
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(code: Int, response: String?) {
        kotlinx.coroutines.MainScope().launch {
            com.abhiumale.kartikmartapp.ui.presentation.payment.PaymentResultRegistry.paymentResults.emit(false)
        }
        NotificationUtils.showNotification(
            this, 
            "Payment Failed! ❌", 
            "Something went wrong during payment."
        )
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
    }
}

