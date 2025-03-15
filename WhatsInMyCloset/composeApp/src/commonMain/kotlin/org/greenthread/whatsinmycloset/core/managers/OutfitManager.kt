package org.greenthread.whatsinmycloset.core.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.repositories.OutfitTags

/*
* The OutfitManager class manages outfits.
* It handle business logic, such as saving, updating, and retrieving outfits.
* */
open class OutfitManager(
    private val outfitTags: OutfitTags
)
{
    private val _savedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val savedOutfits: StateFlow<List<Outfit>> get() = _savedOutfits

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (_savedOutfits.value.isEmpty()) {
                _savedOutfits.value = getOutfits() // Collect Flow once
                println("GreenThread Inside OutfitManager.init (Did refresh cache)")
            }
            println("GreenThread Inside OutfitManager.init (Did not refresh cache)")
        }
    }

    open suspend fun getOutfits(): List<Outfit> {
        return withContext(Dispatchers.IO) {
            emptyList() // Placeholder
        // Fetch outfits from the repository or database
        }
    }

    suspend fun saveOutfit(outfit: Outfit, selectedTags: List<String>? = null) {
        withContext(Dispatchers.IO) {
            // Save the outfit to the repository or database
            if (selectedTags != null) {
                outfitTags.updateTags(selectedTags.toSet())
            }
            _savedOutfits.value = _savedOutfits.value + outfit
        }
    }

    suspend fun removeOutfit(outfitId: String) {
        withContext(Dispatchers.IO) {
            // Remove the outfit from the repository or database
            _savedOutfits.value = _savedOutfits.value.filter { it.id != outfitId }
        }
    }

}
