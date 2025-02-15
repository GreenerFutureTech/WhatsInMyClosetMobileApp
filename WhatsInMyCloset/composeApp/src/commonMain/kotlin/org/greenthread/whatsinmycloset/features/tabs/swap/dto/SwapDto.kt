package org.greenthread.whatsinmycloset.features.tabs.swap.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

@Serializable
data class SwapDto(
    val id: Int,
    val itemId: String,
    val userId: String,
    val mediaUrl: String,
    val status: String,
    val registeredAt: String,
    val updatedAt: String,
)

