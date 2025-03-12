package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.serialization.Serializable

// outfit class matches the ERD
@Serializable
data class Outfit(
    val id: String, // Unique identifier for the outfit
    val userId: String,
    val public: Boolean,
    val favorite: Boolean,
    val mediaURL: String = "",
    val name: String = "", // Name of the outfit (e.g., "Summer Look")
    val items: List<ClothingItem>, // use the ClothingItem class as an outfit will consist of
    // one or more clothing items
    val createdAt: String = ""
)