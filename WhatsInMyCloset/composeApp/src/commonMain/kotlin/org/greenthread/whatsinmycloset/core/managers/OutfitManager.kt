package org.greenthread.whatsinmycloset.core.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first // Import the first function for Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository

open class OutfitManager(
    private val outfitRepository: OutfitRepository,
    private val userManager: UserManager // Inject current user's info
)
{
    private val _cachedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val cachedOutfits: StateFlow<List<Outfit>> get() = _cachedOutfits

    private val currentUser = userManager.currentUser // Get the current user

    init {
        // Start a coroutine to observe changes in the current user
        CoroutineScope(Dispatchers.IO).launch {
            userManager.currentUser.collectLatest { user ->
                if (user != null) {

                    updateOutfits(emptyList())

                    getOutfitsFromRepository()
                }
            }
        }
    }

    fun updateOutfits(outfits: List<Outfit>) {
        _cachedOutfits.value = outfits
    }

    fun getOutfits(): List<Outfit> {
        return cachedOutfits.value
    }

    suspend fun getOutfitsFromDB(): List<Outfit> {
        return withContext(Dispatchers.IO) {
            outfitRepository.getOutfits().first()
        }
    }

    suspend fun getOutfitsRemote(userId: String): Result<List<Outfit>, DataError.Remote> {
        return withContext(Dispatchers.IO) {
            outfitRepository.getOutfitsRemote(userId)
        }
    }

    suspend fun getOutfitsFromRepository() {
        val userId = userManager.currentUser.value?.id
        if (getOutfits().isEmpty()) {
            updateOutfits(getOutfitsFromDB())
            println("GreenThread checking for outfits in Room")
        }

        if (getOutfits().isEmpty() && userId != null) {
            println("GreenThread outfits not found in room, collecting from server. User: $userId")

            val cachedOutfitsRequest = getOutfitsRemote(userId.toString())
            val cachedOutfitsResult = cachedOutfitsRequest.getOrNull()

            cachedOutfitsResult?.let { outfits ->
                outfitRepository.insertOutfits(outfits.map { it.toEntity() })
            }

            // Extract the list of wardrobes from the result
            updateOutfits(cachedOutfitsResult ?: emptyList())

            cachedOutfitsRequest.onError {
                println("Error retrieving outfits from remote: ${it}")
            }
        }

        withContext(Dispatchers.Main) {
        }
    }

    // Create a new outfit
    open suspend fun createOutfit(
        name: String,
        items: List<OutfitItems>,
        tags: List<String> = emptyList()
    ): Outfit {
        val userId = currentUser.value?.id ?: throw IllegalStateException("User ID is required")

        return Outfit(
            id = "",
            name = name,
            userId = userId,
            itemIds = items,
            tags = tags,
            createdAt = ""
        )
    }

    suspend fun saveOutfit(outfit: Outfit): OutfitDto? {
        return try {
            val outfitDto = outfitRepository.saveOutfit(outfit)    // use this for saving outfit in calendar
            if (outfitDto != null) {
                val savedOutfit = outfit.copy(
                    id = outfitDto.id,
                    createdAt = outfitDto.createdAt,
                    name = outfit.name
                )
                _cachedOutfits.update { it + savedOutfit }
                println("Outfit saved successfully: $savedOutfit")
            }
            outfitDto
        } catch (e: Exception) {
            println("Outfit save failure: ${e.message}")
            null
        }
    }

}
