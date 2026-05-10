package com.abhiumale.kartikmartapp.ui.presentation.splashscreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.R
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.abhiumale.kartikmartapp.ui.presentation.registrationscreens.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val offsetY = remember { Animatable(300f) }
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        // 1. Start Animations
        val animationJob = launch {
            launch { offsetY.animateTo(0f, tween(1000)) }
            launch { alpha.animateTo(1f, tween(1000)) }
            launch { scale.animateTo(1f, tween(1000)) }
        }

        // 2. Parallel mein data fetch karein
        if (viewModel.isLoggedIn()) {
            viewModel.fetchUserData()

            // Wait loop: Jab tak data mil na jaye ya 3 seconds na ho jaye
            var retryCount = 0
            while (viewModel.userData == null && retryCount < 30) {
                delay(100)
                retryCount++
            }
        }

        // 3. Wait karein jab tak animation poora na ho jaye
        animationJob.join()

        // Chhota sa extra delay smooth feel ke liye
        delay(500)

        // 4. Final Navigation Logic
        val isLoggedIn = viewModel.isLoggedIn()
        val role = viewModel.userRole?.uppercase() ?: "USER"

        if (isLoggedIn) {
            if (role == "ADMIN") {
                android.util.Log.d("AUTH_DEBUG", "Redirecting to ADMIN")
                navController.navigate(Routs.AdminHomeRouts) {
                    popUpTo(Routs.SplashRouts) { inclusive = true }
                }
            } else {
                android.util.Log.d("AUTH_DEBUG", "Redirecting to USER")
                navController.navigate(Routs.HomeRouts) {
                    popUpTo(Routs.SplashRouts) { inclusive = true }
                }
            }
        } else {
            // Agar logged in nahi hai toh seedha Login par
            navController.navigate(Routs.LoginRouts) {
                popUpTo(Routs.SplashRouts) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.km_splash_icon),
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .alpha(alpha.value)
                .scale(scale.value)
        )
    }
}