package org.greenthread.whatsinmycloset.core.domain.models

import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class User(
    val id: Int? = null,
    val username: String,
    val email: String,
    val name: String,
    val firebaseUuid: String,
    val fcmToken: String? = null,
    val profilePicture: String? = null,
    val type: String? = null,
    val registeredAt: String,
    val updatedAt: String,
    val lastLogin: String
) {
    private val wardrobes = mutableMapOf<String, Wardrobe>() // Maps wardrobe ID to Wardrobe
    private val outfits = mutableMapOf<String, Outfit>() // Maps outfit ID to Outfit
    private val outfitRepositories = mutableMapOf<String, MutableSet<String>>()  // Maps outfit
    // repo user wants to save the outfit in to OutfitRepository class

    fun retrieveUserId() : Int?
    {
        return id
    }

    fun outfitCount() : Int
    {
        // number of outfits user has created - used for outfit id
        return outfits.size // Return the number of outfits
    }

    fun addOutfit(outfit: Outfit, selectedRepositories: List<String>) {
        outfits[outfit.id] = outfit
        outfitRepositories[outfit.id] = selectedRepositories.toMutableSet()
    }

    // remove outfit from all outfits for the user
    // and from the folder it was saved in
    fun removeOutfit(outfitId: String) {
        outfits.remove(outfitId)
        outfitRepositories.remove(outfitId)
    }

    fun getOutfit(outfitId: String): Outfit? {
        return outfits[outfitId]
    }

    fun getAllOutfits(): List<Outfit> {
        return outfits.values.toList()
    }

    /**
     * Get the repositories associated with an outfit.
     */
    fun getRepositoriesForOutfit(outfitId: String): Set<String> {
        return outfitRepositories[outfitId] ?: emptySet()
    }

    fun addWardrobe(wardrobe: Wardrobe) {
        wardrobes[wardrobe.id] = wardrobe
    }

    fun removeWardrobe(wardrobeId: String) {
        wardrobes.remove(wardrobeId)
    }
}