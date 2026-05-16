package com.abhiumale.kartikmartapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationData(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "general", // "order", "payment", "general"
    val isRead: Boolean = false,
    val userId: String = "",
    val metadata: Map<String, String> = emptyMap()
)
