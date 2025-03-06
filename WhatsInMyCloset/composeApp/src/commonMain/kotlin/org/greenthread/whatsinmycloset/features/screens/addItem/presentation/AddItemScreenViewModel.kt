package org.greenthread.whatsinmycloset.features.screens.addItem.presentation

import androidx.lifecycle.ViewModel
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class AddItemScreenViewModel(
    private val wardrobeRepository: WardrobeRepository,
    val wardrobeManager: WardrobeManager
) : ViewModel() {
    private var cachedWardrobes = wardrobeManager.cachedWardrobes
    var defaultWardrobe: Wardrobe? = cachedWardrobes.first()

    init {
    }

    fun getWardrobes() : List<Wardrobe> {
        return cachedWardrobes
    }
}