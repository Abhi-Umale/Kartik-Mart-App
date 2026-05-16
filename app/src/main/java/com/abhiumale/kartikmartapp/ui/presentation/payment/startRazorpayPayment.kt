package com.abhiumale.kartikmartapp.ui.presentation.payment

import com.abhiumale.kartikmartapp.BuildConfig

fun startRazorpayPayment(
    activity: android.app.Activity,
    amount: Double,
    orderId: String,
    userEmail: String,
    userPhone: String
) {
    val checkout = com.razorpay.Checkout()
    checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID)

    try {
        val options = org.json.JSONObject()
        options.put("name", "Kartik Mart")
        options.put("description", "Order ID: $orderId")
        options.put("image", "https://your-logo-url.com/logo.png") // Optional: App ka logo
        options.put("theme.color", "#FFD700") // Kartik Mart ka gold color
        options.put("currency", "INR")

        // Razorpay paise mein amount leta hai (1 INR = 100 Paise)
        options.put("amount", (amount * 100).toInt())

        val prefill = org.json.JSONObject()
        prefill.put("email", userEmail)
        prefill.put("contact", userPhone)
        options.put("prefill", prefill)

        // Security settings
        val retryObj = org.json.JSONObject()
        retryObj.put("enabled", true)
        retryObj.put("max_count", 4)
        options.put("retry", retryObj)

        checkout.open(activity, options)
    } catch (e: Exception) {
        android.util.Log.e("PAYMENT_ERROR", "Razorpay Error: ${e.message}")
    }
}