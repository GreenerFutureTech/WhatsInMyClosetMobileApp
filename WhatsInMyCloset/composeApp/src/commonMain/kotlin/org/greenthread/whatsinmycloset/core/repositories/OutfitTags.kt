package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.Outfit


class OutfitTags (private val user: User) // Pass the logged-in user's account)
{
    // Default repository names
    public val defaultTags = setOf(
        "Business Casuals",
        "Formals",
        "Casuals"
    )

    // StateFlow for default repositories
    private val _outfitTags = MutableStateFlow(defaultTags)
    val outfitTags: StateFlow<Set<String>> = _outfitTags

    // StateFlow for user-created repositories
    private val _userTags = MutableStateFlow<Set<String>>(emptySet())
    val userTags: StateFlow<Set<String>> = _userTags

    // Combined list of default and user-created repositories
    val allTags: Set<String>
        get() = _outfitTags.value + _userTags.value

    // List of saved outfits
    private val _savedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val savedOutfits: StateFlow<List<Outfit>> = _savedOutfits

    /**
     * Add a new user-created repository name.
     */
    fun updateTags(name: String) {
        if (name.isNotBlank() && name !in allTags) {
            _userTags.value = _userTags.value + name
        }
    }

    /**
     * Remove a user-created tag
     */
    fun removeUserRepository(name: String) {
        _userTags.value = _userTags.value - name
    }

    /**
     * Get all saved outfits for the logged in user from the selected tags
     */
    fun getSavedOutfits(): List<Outfit> {
        return _savedOutfits.value
    }

    /**
     * Save an outfit to the repository
     */
    fun saveOutfit(outfit: Outfit,
                   selectedTags: List<String>? = null,
                   selectedTag: String? = null)
    {
        // Save the outfit to the user's account
        if (selectedTags != null || selectedTag != null)
        {
            if (selectedTags != null)
            {
                user.addOutfit(outfit, selectedTags)
            }
            /*else
            {
                account.addOutfit(outfit, selectedFolder)
            }*/
        }
    }

    /**
     * Delete a tag from the outfit for the logged-in user.
     */
    fun removeOutfit(outfitId: String) {
        user.removeOutfit(outfitId)
        _savedOutfits.value = _savedOutfits.value.filter { it.id != outfitId }
    }
}