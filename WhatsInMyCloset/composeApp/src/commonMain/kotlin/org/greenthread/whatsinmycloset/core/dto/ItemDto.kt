package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val id: String,
    val wardrobeId: String,
    val itemType: String,
    val mediaUrl: String,
    val tags: List<String>,
    val condition: String,
    val brand: String,
    val size: String,
    val createdAt: String
)