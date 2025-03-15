package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

/* For serialization and API communication */
@Serializable
data class OutfitDto(
    val id: String, // Unique identifier for the outfit
    val userId: String,
    val public: Boolean,
    val favorite: Boolean,
    val mediaURL: String = "",
    val name: String = "", // Name of the outfit (e.g., "Summer Look")
    val tags: List<String>? = null,
    val items: List<ClothingItem>, // use the ClothingItem class as an outfit will consist of
    // one or more clothing items
    val createdAt: String = ""
)