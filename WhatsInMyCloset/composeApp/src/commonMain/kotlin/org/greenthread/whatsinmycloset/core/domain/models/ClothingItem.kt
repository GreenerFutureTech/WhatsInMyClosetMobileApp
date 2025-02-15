package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ClothingItem(
    val id: String = "",
    val wardrobeId: String = "",
    val itemType: String = "",
    val mediaUrl: String = "",
    val tags: List<String> = listOf(""),
    val createdAt: String = ""
)
