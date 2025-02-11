package org.greenthread.whatsinmycloset.core.domain.models

import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class Account(
    val userId: String, // Unique identifier for the user
    val name: String // User's name
) {
    private val wardrobes = mutableMapOf<String, Wardrobe>() // Maps outfit ID to Outfit
    private val outfits = mutableMapOf<String, Outfit>() // Maps outfit ID to Outfit

    fun addOutfit(outfit: Outfit) {
        outfits[outfit.id] = outfit
    }

    fun removeOutfit(outfitId: String) {
        outfits.remove(outfitId)
    }

    fun getOutfit(outfitId: String): Outfit? {
        return outfits[outfitId]
    }

    fun getAllOutfits(): List<Outfit> {
        return outfits.values.toList()
    }

    fun addWardrobe(wardrobe: Wardrobe) {
        wardrobes[wardrobe.id] = wardrobe
    }

    fun removeWardrobe(wardrobeId: String) {
        wardrobes.remove(wardrobeId)
    }

    fun getWardrobe(wardrobeId: String): Wardrobe? {
        return wardrobes[wardrobeId]
    }

    fun getAllWardrobes(): List<Outfit> {
        return outfits.values.toList()
    }
}