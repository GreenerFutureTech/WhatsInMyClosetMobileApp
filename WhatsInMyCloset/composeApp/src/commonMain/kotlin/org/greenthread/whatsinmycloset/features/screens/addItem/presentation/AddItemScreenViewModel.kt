package org.greenthread.whatsinmycloset.features.screens.addItem.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.isSuccess
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.dto.toClothingItem
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class AddItemScreenViewModel(
    private val wardrobeRepository: WardrobeRepository,
    private val httpRepository: KtorRemoteDataSource,
    val wardrobeManager: WardrobeManager
) : ViewModel() {
    val cachedWardrobes: StateFlow<List<Wardrobe>> = wardrobeManager.cachedWardrobes
    val cachedItems: StateFlow<List<ClothingItem>> = wardrobeManager.cachedItems
    var defaultWardrobe: Wardrobe? = null

    init {
        viewModelScope.launch {
            defaultWardrobe = cachedWardrobes.firstOrNull()?.firstOrNull()
        }
    }

    fun getWardrobes() : List<Wardrobe> {
        return cachedWardrobes.value
    }

    fun addItem(item: ItemDto, image: Any?, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                //BLOB UPLOAD
                val mediaUrl = image?.let { img ->
                    val result = httpRepository.uploadImage("fileName.bmp", img as ByteArray)
                    if (result.isSuccess()) {
                        result.getOrNull()?.let { jsonResponse ->
                            try {
                                Json.decodeFromString<UploadResponse>(jsonResponse).url
                            } catch (e: Exception) {
                                null // Parsing failed
                            }
                        }
                    } else {
                        null // Upload failed
                    }
                }

                // Ensure mediaUrl is copied to item.mediaUrl
                val updatedItem = item.copy(mediaUrl = mediaUrl ?: item.mediaUrl)

                //Item + URL
                val itemUploadRequest = mediaUrl?.let {
                    val result = httpRepository.createItem(updatedItem)
                    if (result.isSuccess() && result.getOrNull() != null) {
                        wardrobeManager.insertItem(result.getOrNull()!!.toClothingItem())
                        result.getOrNull()
                    }
                }

                // ✅ Ensure callback is called on success
                if (itemUploadRequest != null) {
                    callback(true, null)  // Success
                } else {
                    callback(false, "Failed to create item")  // Failure case
                }


                //OLD TEST
/*              val newItem = mediaUrl?.let { item.copy(mediaUrl = it) }

                val createResult = newItem?.let { httpRepository.createItem(it) }

                if (createResult != null) {
                    if (createResult.isSuccess()) {
                        callback(true, null)
                    } else {
                        callback(false, "Failed to create item")
                    }
                }*/

                //FULL ITEM
/*                val itemUploadRequest = image?.let {
                    val result = httpRepository.createItemWithFileUpload(item, image as ByteArray)
                    if (result.isSuccess() && result.getOrNull() != null) {
                        wardrobeManager.insertItem(result.getOrNull()!!.toClothingItem())
                        result.getOrNull()
                    }
                }*/


            } catch (e: Exception) {
                callback(false, e.message)
            }
        }
    }
}

@Serializable
data class UploadResponse(
    val url: String  // Must match JSON key ("url")
)