package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.Outfit


class OutfitRepository (private val account: Account) // Pass the logged-in user's account)
{
    // Default repository names
    public val defaultRepositories = setOf(
        "Business Casuals",
        "Formals",
        "Casuals",
        "Public Outfits"
    )

    // StateFlow for default repositories
    private val _outfitFolders = MutableStateFlow(defaultRepositories)
    val outfitFolders: StateFlow<Set<String>> = _outfitFolders

    // StateFlow for user-created repositories
    private val _userRepositories = MutableStateFlow<Set<String>>(emptySet())
    val userRepositories: StateFlow<Set<String>> = _userRepositories

    // Combined list of default and user-created repositories
    val allRepositories: Set<String>
        get() = _outfitFolders.value + _userRepositories.value

    fun getAllRepos() : Set<String>
    {
        return allRepositories
    }

    // List of saved outfits
    private val _savedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val savedOutfits: StateFlow<List<Outfit>> = _savedOutfits

    /**
     * Add a new user-created repository name.
     */
    fun addUserRepository(name: String) {
        if (name.isNotBlank() && name !in allRepositories) {
            _userRepositories.value = _userRepositories.value + name
        }
    }

    /**
     * Remove a user-created repository name.
     */
    fun removeUserRepository(name: String) {
        _userRepositories.value = _userRepositories.value - name
    }

    /**
     * Get all saved outfits for the logged in user from the selected repository
     */
    fun getSavedOutfits(): List<Outfit> {
        return _savedOutfits.value
    }

    /**
     * Save an outfit to the repository
     */
    fun saveOutfit(outfit: Outfit,
                   selectedFolders: List<String>? = null,
                   selectedFolder: String? = null)
    {
        // Save the outfit to the user's account
        if (selectedFolders != null || selectedFolder != null)
        {
            if (selectedFolders != null)
            {
                account.addOutfit(outfit, selectedFolders)
            }
            /*else
            {
                account.addOutfit(outfit, selectedFolder)
            }*/
        }
    }

    /**
     * Delete a saved outfit for the logged-in user.
     */
    fun removeOutfit(outfitId: String) {
        account.removeOutfit(outfitId)
        _savedOutfits.value = _savedOutfits.value.filter { it.id != outfitId }
    }
}