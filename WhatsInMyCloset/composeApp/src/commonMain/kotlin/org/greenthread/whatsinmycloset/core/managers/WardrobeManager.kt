package org.greenthread.whatsinmycloset.core.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class WardrobeManager(
    private val wardrobeRepository: WardrobeRepository
) {
    public var cachedWardrobes = emptyList<Wardrobe>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (cachedWardrobes.isEmpty()){
                cachedWardrobes = getWardrobes() // Collect Flow once
                println("GreenThread Inside WardrobeManager.init (Did refresh cache)")
            }
            println("GreenThread Inside WardrobeManager.init (Did not refresh cache)")
        }
    }

    suspend fun getWardrobes(): List<Wardrobe> {
        return withContext(Dispatchers.IO) {
            wardrobeRepository.getWardrobes().first()
        }
    }
}