package org.greenthread.whatsinmycloset.core.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.models.toEntity
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.persistence.toWardrobeEntity
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

open class WardrobeManager(
    private val wardrobeRepository: WardrobeRepository,
    private val userManager: UserManager
) {
    private val _cachedWardrobes = MutableStateFlow<List<Wardrobe>>(emptyList())
    val cachedWardrobes: StateFlow<List<Wardrobe>> = _cachedWardrobes

    private val _cachedItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val cachedItems: StateFlow<List<ClothingItem>> = _cachedItems

    init {
        // Start a coroutine to observe changes in the current user
        CoroutineScope(Dispatchers.IO).launch {
            userManager.currentUser.collectLatest { user ->
                if (user != null) {

                    updateWardrobes(emptyList())
                    updateItems(emptyList())

                    getWardrobesFromRepository()
                    getItemsFromRepository()
                }
                else
                {
                    updateWardrobes(emptyList())
                    updateItems(emptyList())
                }
            }
        }
    }

    fun updateWardrobes(wardrobes: List<Wardrobe>) {
        _cachedWardrobes.value = wardrobes
    }

    fun updateItems(items: List<ClothingItem>) {
        _cachedItems.value = items
    }

    fun getWardrobes(): List<Wardrobe> {
        return cachedWardrobes.value
    }

    fun getItems(): List<ClothingItem> {
        return cachedItems.value
    }

    fun getItemsInOutfit(outfitItems: List<OutfitItems>): List<ClothingItem> {
        val itemIds = outfitItems.map { it.id ?: "" } // Extract all IDs
        return cachedItems.value.filter { clothingItem ->
            clothingItem.id in itemIds
        }
    }

    fun getItemsByCategory(category: ClothingCategory): List<ClothingItem> {
        return cachedItems.value.filter { it.itemType == category }
    }

    // Suspending function to load wardrobes
    suspend fun getWardrobesFromRepository() {
        val userId = userManager.currentUser.value?.id
        if (getWardrobes().isEmpty()) {
            // Try loading from local database (Room)
            updateWardrobes(getWardrobesFromDB(userId!!))
            println("GreenThread checking for wardrobes in Room")
        }

        // If not found locally, fetch from remote
        if (getWardrobes().isEmpty() && userId != null) {
            println("GreenThread wardrobes not found in room, collecting from server. User: $userId")

            val cachedWardrobesRequest = getWardobesRemote(userId.toString())
            val cachedWardrobesResult = cachedWardrobesRequest.getOrNull()

            //Insert each wardrobe from the server into Room
            if (cachedWardrobesResult != null) {
                for (wardrobe in cachedWardrobesResult) {
                    wardrobeRepository.insertWardrobe(wardrobe.toWardrobeEntity())
                }
            }

            // Extract the list of wardrobes from the result
            updateWardrobes(cachedWardrobesResult ?: emptyList())

            cachedWardrobesRequest.onError {
                println("Error retrieving wardrobes from remote: ${it}")
            }
        }

        // If required, update the UI or state here on the main thread after data is loaded
        withContext(Dispatchers.Main) {
            // Perform actions on the UI, for example:
            // updateUI(cachedWardrobes)
        }
    }

    // Function to simulate fetching wardrobes from local DB (Room)
    suspend fun getWardrobesFromDB(userId: Int): List<Wardrobe> {
        return withContext(Dispatchers.IO) {
            // Your DB fetching logic here
            // This is just an example and should be replaced with your actual DB fetch logic
            wardrobeRepository.getWardrobes(userId).first()
        }
    }

    // Function to fetch wardrobes from the server
    suspend fun getWardobesRemote(userId: String): Result<List<Wardrobe>, DataError.Remote> {
        return withContext(Dispatchers.IO) {
            wardrobeRepository.getWardrobesRemote(userId)
        }
    }

    suspend fun insertWardrobe(wardrobe: WardrobeEntity) {
        wardrobeRepository.insertWardrobe((wardrobe))
    }



    ///ITEMS

    // Suspending function to load wardrobes
    suspend fun getItemsFromRepository() {
        val userId = userManager.currentUser.value?.id
        if (getItems().isEmpty()) {
            // Try loading from local database (Room)
            updateItems(getItemsFromDB())
            println("GreenThread checking for items in Room")
        }

        // If not found locally, fetch from remote
        if (getItems().isEmpty() && userId != null) {
            println("GreenThread items not found in room, collecting from server. User: $userId")

            val cachedItemsRequest = getItemsRemote(userId.toString())
            val cachedItemsResult = cachedItemsRequest.getOrNull()

            //Insert each wardrobe from the server into Room
            if (cachedItemsResult != null) {
                for (item in cachedItemsResult) {
                    wardrobeRepository.insertItem(item.toEntity())
                }
            }

            // Extract the list of wardrobes from the result (safe handling of Result)
            updateItems(cachedItemsRequest.getOrNull() ?: emptyList())

            // Logging error if remote fetching fails
            cachedItemsRequest.onError {
                println("Error retrieving wardrobes from remote: ${it}")
            }
        }
    }

    // Function to simulate fetching wardrobes from local DB (Room)
    suspend fun getItemsFromDB(): List<ClothingItem> {
        return withContext(Dispatchers.IO) {
            // Your DB fetching logic here
            // This is just an example and should be replaced with your actual DB fetch logic
            wardrobeRepository.getItems().first()
        }
    }

    // Function to fetch wardrobes from the server
    suspend fun getItemsRemote(userId: String): Result<List<ClothingItem>, DataError.Remote> {
        return withContext(Dispatchers.IO) {
            wardrobeRepository.getItemsRemote(userId)
        }
    }

    suspend fun insertItem(clothingItem: ClothingItem) {
        //add to cached items flow
        _cachedItems.value += clothingItem
        wardrobeRepository.insertItem((clothingItem.toEntity()))
    }
}