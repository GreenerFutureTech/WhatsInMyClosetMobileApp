package org.greenthread.whatsinmycloset.core.domain.models

data class Outfit(
    val id: String, // Unique identifier for the outfit
    val name: String, // Name of the outfit (e.g., "Summer Look")
    val itemIds: Set<String> // IDs of the clothing items in this outfit
)