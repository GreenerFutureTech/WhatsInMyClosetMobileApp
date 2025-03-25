package org.greenthread.whatsinmycloset.core.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first // Import the first function for Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.EmptyResult
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.models.toEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitItem
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository
import org.greenthread.whatsinmycloset.core.domain.EmptyResult.Success
import org.greenthread.whatsinmycloset.core.domain.EmptyResult.Error

/*
* Manages business logic for outfits, including creating, saving, updating, and retrieving outfits.
* Acts as an intermediary between the UI (ViewModel) and the repository layer.
* */

open class OutfitManager(
    private val outfitRepository: OutfitRepository,
    private val userManager: UserManager // Inject current user's info
)
{
    private val _savedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val savedOutfits: StateFlow<List<Outfit>> get() = _savedOutfits

    private val currentUser = userManager.currentUser // Get the current user

    // Get outfits for the current user
    open suspend fun getOutfits(): List<Outfit> {
        return withContext(Dispatchers.IO) {
            if (currentUser != null) {
                // Use user ID if available, otherwise fall back to a default value
                val userId = currentUser.value?.id ?: throw IllegalStateException("User ID is required")
                outfitRepository.getOutfits(userId).first() // Collect the first emission of the Flo
            } else {
                emptyList() // Return an empty list if no user is logged in
            }
        }
    }

    // Create a new outfit
    open suspend fun createOutfit(
        name: String,
        items: Map<String, OffsetData>,
        tags: List<String> = emptyList()
    ): Outfit {
        val userId = currentUser.value?.id ?: throw IllegalStateException("User ID is required") // TODO fix this
        //throw IllegalStateException("User ID is required") (commented for testing)

        return Outfit(
            id = generateOutfitId(),
            name = name,
            creatorId = userId,
            items = items,
            tags = tags,
            calendarDates = emptyList(),
            createdAt = Clock.System.now().toLocalDateTime
                (TimeZone.currentSystemDefault()).toString()
        )
    }


    suspend fun saveOutfit(outfit: Outfit): Boolean {
        val userId = userManager.currentUser.value?.id ?: return false

        return try {
            if (outfitRepository.saveOutfit(outfit)) {
                _savedOutfits.update { it + outfit }
                println("Outfit saved successfully: ${outfit.id}")
                true
            } else {
                println("Outfit save failure: ${outfit.id}")
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeOutfit(outfitId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val userId = userManager.currentUser.value?.id
                    ?: throw IllegalStateException("User not logged in")

                outfitRepository.getOutfits(userId).first()
                    .find { it.id == outfitId }
                    ?.let { outfit ->
                        outfitRepository.deleteOutfit(outfit.toEntity())
                        _savedOutfits.value = _savedOutfits.value.filter { it.id != outfitId }
                        true
                    } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }

    // Helper function to generate a unique outfit ID
    private fun generateOutfitId(): String {
        return "outfit_${
            Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault()).toString()}"
    }

}
