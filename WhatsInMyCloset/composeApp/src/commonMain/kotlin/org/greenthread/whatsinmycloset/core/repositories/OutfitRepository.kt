package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.Outfit

class OutfitRepository {

    private val _outfitFolders = MutableStateFlow(
        listOf("Business Casuals", "Fancy", "Formals", "Casuals", "My Public Outfits")
    )
    val outfitFolders: StateFlow<List<String>> = _outfitFolders

    // List of saved outfits
    private val _savedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val savedOutfits: StateFlow<List<Outfit>> = _savedOutfits

    /**
     * Add a new outfit folder (if it doesn't already exist)
     */
    fun addFolder(folderName: String) {
        if (folderName.isNotBlank() && folderName !in _outfitFolders.value) {
            _outfitFolders.value = _outfitFolders.value + folderName
        }
    }

    /**
     * Delete an outfit folder
     */
    fun removeFolder(folderName: String) {
        _outfitFolders.value = _outfitFolders.value - folderName
    }

    /**
     * Get all saved outfits
     */
    fun getSavedOutfits(): List<Outfit> {
        return _savedOutfits.value
    }

    /**
     * Save an outfit to the repository
     */
    fun saveOutfit(outfit: Outfit) {
        // Check if the outfit already exists (based on ID)
        if (_savedOutfits.value.none { it.id == outfit.id }) {
            _savedOutfits.value = _savedOutfits.value + outfit
        }
    }

    /**
     * Delete a saved outfit
     */
    fun removeOutfit(outfitId: String) {
        _savedOutfits.value = _savedOutfits.value.filter { it.id != outfitId }
    }
}