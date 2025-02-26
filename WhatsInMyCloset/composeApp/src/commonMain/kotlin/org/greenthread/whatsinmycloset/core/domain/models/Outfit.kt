package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.serialization.Serializable

// outfit will have an ID and a name
// It will consist of ClothingItem which has attributes specific to the ClothingItem
// such as the id, name, category (tops, bottoms etc.) image and tags
@Serializable
data class Outfit(
    val id: String, // Unique identifier for the outfit
    val name: String, // Name of the outfit (e.g., "Summer Look")
    val itemIds: List<ClothingItem> // use the ClothingItem class as an outfit will consist of
    // one or more clothing items
)