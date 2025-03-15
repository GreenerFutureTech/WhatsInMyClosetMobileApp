package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.PrimaryKey
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

/* This class is defined for Room database */
data class OutfitEntity(
    @PrimaryKey val id: String, // Matches backend UUID
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