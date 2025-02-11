package org.greenthread.whatsinmycloset.core.domain.models

data class ClothingItem(
    val id: String, // Unique identifier for the item
    val name: String, // Name of the item (e.g., "Red Dress")
    val pictureUrl: String?, // URL or path to the picture (nullable)
    val tags: Set<String> // Tags for characteristics (e.g., "red", "fancy", "rainy")
)