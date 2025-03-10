package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

@Serializable
data class SwapDto(
    val id: String,
    val itemId: ItemDto,
    val userId: Int,
    val status: String,
    val registeredAt: String,
    val updatedAt: String
)

