package org.greenthread.whatsinmycloset.core.domain.models

import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class User(
    val id: Int? = null,
    val username: String,
    val email: String,
    val name: String,
    val firebaseUuid: String?,
    val fcmToken: String? = null,
    val profilePicture: String? = null,
    val type: String? = null,
    val registeredAt: String,
    val updatedAt: String,
    val lastLogin: String,
    val friends: List<Friend>? = null
) {
    private val wardrobes = mutableMapOf<String, Wardrobe>() // Maps wardrobe ID to Wardrobe
    private val outfits = mutableMapOf<String, Outfit>() // Maps outfit ID to Outfit
    private val outfitTags = mutableMapOf<String, MutableSet<String>>()  // Maps outfit
    // repo user wants to save the outfit in to OutfitRepository class


    fun toUserDto(): UserDto = UserDto(
        id = id,
        name = name,
        username = username,
        email = email,
        firebaseUid = firebaseUuid,
        profilePicture = profilePicture,
        type = type,
        registeredAt = registeredAt,
        updatedAt = updatedAt,
        lastLogin = lastLogin,
        friends = friends
    )

    fun retrieveUserId() : Int?
    {
        return id
    }

    fun toDto(): UserDto {
        return UserDto(
            id = id,
            username = username,
            email = email,
            name = name,
            firebaseUid = firebaseUuid,
            fcmToken = fcmToken,
            profilePicture = profilePicture,
            type = type,
            registeredAt = registeredAt,
            updatedAt = updatedAt,
            lastLogin = lastLogin,
            friends = friends
        )
    }

    fun copy(
        id: Int? = this.id,
        username: String = this.username,
        email: String = this.email,
        name: String = this.name,
        firebaseUuid: String = this.firebaseUuid,
        fcmToken: String? = this.fcmToken,
        profilePicture: String? = this.profilePicture,
        type: String? = this.type,
        registeredAt: String = this.registeredAt,
        updatedAt: String = this.updatedAt,
        lastLogin: String = this.lastLogin,
        friends: List<Friend>? = this.friends
    ): User {
        return User(
            id = id,
            username = username,
            email = email,
            name = name,
            firebaseUuid = firebaseUuid,
            fcmToken = fcmToken,
            profilePicture = profilePicture,
            type = type,
            registeredAt = registeredAt,
            updatedAt = updatedAt,
            lastLogin = lastLogin,
            friends = friends
        )
    }

    fun addOutfit(outfit: Outfit, selectedTags: List<String>) {
        outfits[outfit.id] = outfit
        outfitTags[outfit.id] = selectedTags.toMutableSet()
    }

    // remove outfit from all outfits for the user
    // and from the folder it was saved in
    fun removeOutfit(outfitId: String) {
        outfits.remove(outfitId)
        outfitTags.remove(outfitId)
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
        return outfitTags[outfitId] ?: emptySet()
    }

    fun addWardrobe(wardrobe: Wardrobe) {
        wardrobes[wardrobe.id] = wardrobe
    }

    fun removeWardrobe(wardrobeId: String) {
        wardrobes.remove(wardrobeId)
    }
}