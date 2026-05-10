package com.abhiumale.kartikmartapp.domain.model

import com.abhiumale.kartikmartapp.ui.navigation.Routs
import kotlinx.serialization.Serializable

@Serializable
data class navItem (
    val title: String,
    val icon: Int,
    val route: Routs,
)