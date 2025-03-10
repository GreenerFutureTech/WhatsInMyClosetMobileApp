package org.greenthread.whatsinmycloset.features.screens.addItem.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.isSuccess
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class AddItemScreenViewModel(
    private val wardrobeRepository: WardrobeRepository,
    private val httpRepository: KtorRemoteDataSource,
    val wardrobeManager: WardrobeManager
) : ViewModel() {
    private var cachedWardrobes = wardrobeManager.cachedWardrobes
    var defaultWardrobe: Wardrobe? = cachedWardrobes.first()

    init {
    }

    fun getWardrobes() : List<Wardrobe> {
        return cachedWardrobes
    }

    fun addItem(item: ItemDto, image: ByteArray?, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val mediaUrl = image?.let {
                    val result = httpRepository.uploadFile(it)
                    if (result.isSuccess()) result.getOrNull() else null
                }

                val newItem = mediaUrl?.let { item.copy(mediaUrl = it) }

                val createResult = newItem?.let { httpRepository.createItem(it) }

                if (createResult != null) {
                    if (createResult.isSuccess()) {
                        callback(true, null)
                    } else {
                        callback(false, "Failed to create item")
                    }
                }
            } catch (e: Exception) {
                callback(false, e.message)
            }
        }
    }
}