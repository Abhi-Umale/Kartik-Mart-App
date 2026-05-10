package com.abhiumale.kartikmartapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AdminHomeScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AddProductScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.UserListScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AdminProductsScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AdminOrdersScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AdminAnalyticsScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AdminCategoriesScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AdminBrandsScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AdminCouponsScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AdminSettingsScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.AdminProfileScreen
import com.abhiumale.kartikmartapp.ui.presentation.adminPanel.components.AdminPlaceholderScreen
import com.abhiumale.kartikmartapp.ui.presentation.cartscreen.CartScreen
import com.abhiumale.kartikmartapp.ui.presentation.homescreen.HomeScreen
import com.abhiumale.kartikmartapp.ui.presentation.orderscreen.OrdersScreen
import com.abhiumale.kartikmartapp.ui.presentation.productdetailscreen.ProductDetailScreen
import com.abhiumale.kartikmartapp.ui.presentation.profilescreen.ProfileScreen
import com.abhiumale.kartikmartapp.ui.presentation.registrationscreens.LoginScreen
import com.abhiumale.kartikmartapp.ui.presentation.registrationscreens.RegisterScreen
import com.abhiumale.kartikmartapp.ui.presentation.splashscreen.SplashScreen
import com.abhiumale.kartikmartapp.ui.presentation.all_product_screen.TopDealsScreen
import com.abhiumale.kartikmartapp.ui.presentation.checkout.CheckoutScreen
import com.abhiumale.kartikmartapp.ui.presentation.order_tracking.OrderTrackingScreen
import com.abhiumale.kartikmartapp.ui.presentation.payment.PaymentScreen
import java.net.URLDecoder


@Composable
fun NavGraph() {

    val  navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routs.SplashRouts
    ) {

        composable<Routs.SplashRouts> {
            SplashScreen(navController)
        }

        composable<Routs.LoginRouts> {
            LoginScreen(navController)
        }

        composable<Routs.RegisterRouts> {
            RegisterScreen(navController)
        }

        composable<Routs.HomeRouts> {
            HomeScreen(navController)
        }

        composable<Routs.AdminHomeRouts> {
            AdminHomeScreen(navController)
        }

        composable<Routs.AddProductRouts> {
            AddProductScreen(navController)
        }

        composable<Routs.AdminUsersRouts> {
            UserListScreen(navController)
        }

        composable<Routs.AdminProductsRouts> { AdminProductsScreen(navController) }
        composable<Routs.AdminOrdersRouts> { AdminOrdersScreen(navController) }
        composable<Routs.AdminAnalyticsRouts> { AdminAnalyticsScreen(navController) }
        composable<Routs.AdminCategoriesRouts> { AdminCategoriesScreen(navController) }
        composable<Routs.AdminBrandsRouts> { AdminBrandsScreen(navController) }
        composable<Routs.AdminCouponsRouts> { AdminCouponsScreen(navController) }
        composable<Routs.AdminSettingsRouts> { AdminSettingsScreen(navController) }
        composable<Routs.AdminProfileRouts> { AdminProfileScreen(navController) }

        composable<Routs.CartRouts> {
            CartScreen(navController)
        }

        composable<Routs.TopDealsRouts> {
            TopDealsScreen(navController = navController)
        }


        composable<Routs.OrdersRouts> {
            OrdersScreen(navController)
        }

        composable<Routs.ProductDetailRouts> { backStackEntry ->

            val args = backStackEntry.toRoute<Routs.ProductDetailRouts>()

            // Decoding image URL before passing to screen
            val decodedImage = try {
                URLDecoder.decode(args.image, "UTF-8")
            } catch (e: Exception) {
                args.image
            }

            ProductDetailScreen(
                navController = navController,
                productId = args.productId.toInt(),
                name = args.name,
                price = args.price,
                mrp = args.mrp,
                image = decodedImage,
                category = args.category
            )

        }

        composable<Routs.ProfileRouts>{
            ProfileScreen(navController)
        }

        composable <Routs.CheckoutScreen>{
            CheckoutScreen(
                onNavigateToPayment = { orderId, amount ->
                    navController.navigate(Routs.PaymentRouts(orderId, amount)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Routs.PaymentRouts> { backStackEntry ->
            val args = backStackEntry.toRoute<Routs.PaymentRouts>()
            PaymentScreen(
                navController = navController,
                orderId = args.orderId,
                amount = args.amount
            )
        }

        // Order Tracking Screen
        composable<Routs.OrderTracking> { backStackEntry ->
            // Data retrieve karna bina bundle check kiye
            val trackingArgs: Routs.OrderTracking = backStackEntry.toRoute()

            OrderTrackingScreen(
                orderId = trackingArgs.orderId,
                eta = trackingArgs.estimatedTime
            )
        }

    }
}
