package org.greenthread.whatsinmycloset.core.domain.models

class Account(
    val id: Int? = null,
    val username: String,
    val email: String,
    val name: String,
    val firebaseUuid: String,
    val profilePicture: String? = null,
    val registeredAt: String,
    val updatedAt: String,
    val lastLogin: String
) {
    private val outfits = mutableMapOf<String, Outfit>() // Maps outfit ID to Outfit

    fun addOutfit(outfit: Outfit) {
        outfits[outfit.id] = outfit
    }

    fun getOutfit(outfitId: String): Outfit? {
        return outfits[outfitId]
    }

    fun getAllOutfits(): List<Outfit> {
        return outfits.values.toList()
    }
}

data class ProfileDto(
    val id: Int? = null,
    val username: String,
    val email: String,
    val name: String,
    val firebaseUid: String,
    val profilePicture: String? = null,
    val registeredAt: String,
    val updatedAt: String,
    val lastLogin: String
)