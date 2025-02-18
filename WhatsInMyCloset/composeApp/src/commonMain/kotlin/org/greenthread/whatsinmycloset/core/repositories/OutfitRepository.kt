package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OutfitRepository {

    private val _outfitFolders = MutableStateFlow(
        listOf("Business Casuals", "Fancy", "Formals", "Casuals", "My Public Outfits")
    )
    val outfitFolders: StateFlow<List<String>> = _outfitFolders

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
}