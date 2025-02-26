package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.Outfit

/**
 * Users can save or review past outfits
 */
class OutfitHistoryViewModel : ViewModel() {
    private val _pastOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val pastOutfits: StateFlow<List<Outfit>> = _pastOutfits

    fun saveOutfit(outfit: Outfit) {
        _pastOutfits.value = _pastOutfits.value + outfit
    }

    fun deleteOutfit(outfit: Outfit) {
        _pastOutfits.value = _pastOutfits.value - outfit
    }
}
