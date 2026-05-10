package com.abhiumale.kartikmartapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.abhiumale.kartikmartapp.ui.navigation.NavGraph
import com.abhiumale.kartikmartapp.ui.theme.KartikMartAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KartikMartAppTheme {
               NavGraph()
            }
        }
    }
}

