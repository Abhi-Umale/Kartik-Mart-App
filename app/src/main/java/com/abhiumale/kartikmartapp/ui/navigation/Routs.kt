package com.abhiumale.kartikmartapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routs {

    @Serializable
    object SplashRouts : Routs()

    @Serializable
    object LoginRouts : Routs()

    @Serializable
    object RegisterRouts : Routs()

    @Serializable
    object HomeRouts : Routs()
    @Serializable
    object AdminHomeRouts : Routs()
    @Serializable
    object AdminUsersRouts : Routs()
    @Serializable
    object AdminProductsRouts : Routs()
    @Serializable
    object AdminOrdersRouts : Routs()
    @Serializable
    object AdminAnalyticsRouts : Routs()
    @Serializable
    object AdminCategoriesRouts : Routs()
    @Serializable
    object AdminBrandsRouts : Routs()
    @Serializable
    object AdminCouponsRouts : Routs()
    @Serializable
    object AdminSettingsRouts : Routs()
    @Serializable
    object AdminProfileRouts : Routs()
    @Serializable
    object AddProductRouts : Routs()
    @Serializable
    object CartRouts : Routs()
    @Serializable
    object TopDealsRouts : Routs()
    @Serializable
    object OrdersRouts : Routs()
    @Serializable
    data class CheckoutScreen(val productId: String? = null) : Routs()
    @Serializable
    data class OrderTracking(
        val orderId: String,
        val estimatedTime: String = "30 mins"
    ) : Routs()// Arguments pass karne ke liye data class

    @Serializable
    data class OrderConfirmationRouts(val orderId: String) : Routs()

    @Serializable
    data class PaymentRouts(
        val orderId: String,
        val amount: Double,
        val address: String
    ) : Routs()

    @Serializable
    data class ProductDetailRouts(
        val productId: Long,
        val name: String,
        val price: Double,
        val mrp: Double,
        val image: String,
        val category: String
    ) : Routs()

    @Serializable
    object ProfileRouts : Routs()

    @Serializable
    object NotificationRouts : Routs()

    @Serializable
    object AdminNotificationRouts : Routs()
}
