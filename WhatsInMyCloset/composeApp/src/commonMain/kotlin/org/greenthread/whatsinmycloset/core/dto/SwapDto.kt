package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class SwapDto(
    val id: String,
    val itemId: ItemDto,
    val userId: Int,
    val status: String,
    val registeredAt: String,
    val updatedAt: String? = ""
)

@Serializable
data class SwapStatusDto(
    val id: String,
    val itemId: String,
    val userId: Int,
    val status: String,
    val registeredAt: String,
    val updatedAt: String? = ""
)

@Serializable
data class CreateSwapRequestDto(
    val id: String? = null,
    val itemId: String,
    val userId: Int,
    val status: String,
    val registeredAt: String,
    val updatedAt: String? = ""
)


