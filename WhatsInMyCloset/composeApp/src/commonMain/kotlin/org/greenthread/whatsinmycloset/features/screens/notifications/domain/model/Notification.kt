package org.greenthread.whatsinmycloset.features.screens.notifications.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: Int, // Changed from String to Int to match backend
    val userId: Int, // Added to match backend
    val type: String, // Added to match backend
    val title: String,
    val body: String, // Changed from `message` to `body` to match backend
    val extraData: Map<String, String>? = null, // Added to match backend
    val isRead: Boolean, // Added to match backend
    val createdAt: String // Added to match backend (assuming it's a string in ISO format)
)