package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class OtherUserSwapDto(
    val id: String,
    val itemId: ItemDto,
    val userId: Int,
    val status: String,
    val condition: String,
    val brand: String,
    val registeredAt: String,
    val updatedAt: String
)