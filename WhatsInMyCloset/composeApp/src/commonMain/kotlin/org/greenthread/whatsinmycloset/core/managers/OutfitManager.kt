package org.greenthread.whatsinmycloset.core.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first // Import the first function for Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.persistence.OutfitItemJoin
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository
import org.greenthread.whatsinmycloset.core.repositories.OutfitTags

/*
* Manages business logic for outfits, including creating, saving, updating, and retrieving outfits.
* Acts as an intermediary between the UI (ViewModel) and the repository layer.
* */

open class OutfitManager(
    private val outfitRepository: OutfitRepository,
    private val outfitTags: OutfitTags,
    private val userManager: UserManager // Inject current user's info
)
{
    private val _savedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val savedOutfits: StateFlow<List<Outfit>> get() = _savedOutfits

    private val currentUser = userManager.getUser() // Get the current user

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (_savedOutfits.value.isEmpty()) {
                _savedOutfits.value = getOutfits() // Collect Flow once
                println("GreenThread Inside OutfitManager.init (Did refresh cache)")
            }
            println("GreenThread Inside OutfitManager.init (Did not refresh cache)")
        }
    }

    // Get outfits for the current user
    open suspend fun getOutfits(): List<Outfit> {
        return withContext(Dispatchers.IO) {
            if (currentUser != null) {
                // Use user ID if available, otherwise fall back to a default value
                val userId = currentUser.id ?: throw IllegalStateException("User ID is required")
                outfitRepository.getOutfits(userId).first() // Collect the first emission of the Flo
            } else {
                emptyList() // Return an empty list if no user is logged in
            }
        }
    }

    // Create a new outfit
    open suspend fun createOutfit(
        name: String,
        clothingItems: List<ClothingItem>,
        tags: List<String> = emptyList(),
        isPublic: Boolean = false,
        favourite: Boolean = true
    ): Outfit {
        val userId = currentUser?.id ?: throw IllegalStateException("User ID is required")
        return Outfit(
            id = generateOutfitId(), // Generate a unique ID for the outfit
            name = name,
            userId = userId,
            items = clothingItems,
            tags = tags,
            public = isPublic,
            favorite = favourite
        )
    }

    suspend fun saveOutfit(outfit: Outfit, selectedTags: List<String>?) {
        val outfitEntity = outfit.toEntity(outfit.userId)
        outfitRepository.insertOutfit(outfitEntity)

        // Save the relationship between the outfit and its items
        outfit.items.forEach { item ->
            // join items to outfit when saving (many-to-many relationship)
            outfitRepository.insertOutfitItemJoin(OutfitItemJoin(outfit.id, item.id))
        }

        // Update tags if provided
        if (selectedTags != null) {
            outfitTags.updateTags(selectedTags.toSet())
        }

        // Update the cached list of outfits
        _savedOutfits.value = _savedOutfits.value + outfit
    }

    suspend fun removeOutfit(outfitId: String) {
        withContext(Dispatchers.IO) {
            val currentUser = userManager.getUser() // Get the current user
            if (currentUser != null) {
                // Use user ID if available, otherwise fall back to username
                val userId = currentUser.id ?: throw IllegalStateException("User ID is required")
                val outfitEntity = outfitRepository.getOutfitById(outfitId, userId)
                if (outfitEntity != null) {
                    outfitRepository.deleteOutfit(outfitEntity)
                }

                // Update the cached list of outfits
                _savedOutfits.value = _savedOutfits.value.filter { it.id != outfitId }
            }
        }
    }

    // Helper function to generate a unique outfit ID
    private fun generateOutfitId(): String {
        return "outfit_${LocalDate}"
    }

}
