package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.greenthread.whatsinmycloset.core.data.daos.WardrobeDao
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.persistence.toWardrobe
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class MockClothingItemViewModel(wardrobeManager: WardrobeManager) :
    ClothingItemViewModel(wardrobeRepository = MockWardrobeRepository(),
    wardrobeManager
)
{
    init {
        // Add some mock data for the preview
        addClothingItems(
            listOf(
                ClothingItem(id = "1", name = "Top 1", itemType = ClothingCategory.TOPS),
                ClothingItem(id = "2", name = "Bottom 1", itemType = ClothingCategory.BOTTOMS),
                ClothingItem(id = "3", name = "Shoes 1", itemType = ClothingCategory.FOOTWEAR)
            )
        )
    }
}

class MockOutfitViewModel (
    initialSelectedFolder: String? = null,
    initialSelectedFolders: List<String> = emptyList(),
    initialIsPublic: Boolean = false
): OutfitViewModel()
{

    private val outfitRepository = OutfitRepository()
    override val outfitFolders: StateFlow<List<String>> = outfitRepository.outfitFolders

    override val selectedFolder: StateFlow<String?> = MutableStateFlow(initialSelectedFolder).asStateFlow()
    override val selectedFolders: StateFlow<List<String>> = MutableStateFlow(initialSelectedFolders).asStateFlow()
    override val isPublic: StateFlow<Boolean> = MutableStateFlow(initialIsPublic).asStateFlow()

    override fun updateSelectedFolder(folder: String?) {
        (selectedFolder as MutableStateFlow).value = folder
    }

    override fun updateSelectedFolders(folder: String) {
        val currentFolders = (selectedFolders as MutableStateFlow).value.toMutableList()
        if (currentFolders.contains(folder)) {
            currentFolders.remove(folder)
        } else {
            currentFolders.add(folder)
        }
        (selectedFolders as MutableStateFlow).value = currentFolders
    }

    override fun toggleIsPublic(isPublic: Boolean) {
        (this.isPublic as MutableStateFlow).value = isPublic
    }

    override fun addFolder(folderName: String) {
        val currentFolders = (outfitFolders as MutableStateFlow).value.toMutableList()
        currentFolders.add(folderName)
        (outfitFolders as MutableStateFlow).value = currentFolders
    }
}

class MockWardrobeRepository : WardrobeRepository(wardrobeDao = MockWardrobeDao()) {

    override fun getWardrobes(): Flow<List<Wardrobe>> {
        return wardrobeDao
            .getWardrobes()
            .map { wardrobeEntities ->
                wardrobeEntities.map { it.toWardrobe() }
            }
    }

}

class MockWardrobeManager(
    private val wardrobeRepository: WardrobeRepository = MockWardrobeRepository()
) : WardrobeManager(wardrobeRepository) {

    override suspend fun getWardrobes(): List<Wardrobe> {
        // Return cached wardrobes or mock data if empty
        return withContext(Dispatchers.IO) {
            wardrobeRepository.getWardrobes().first()
        }
    }
}


class MockWardrobeDao : WardrobeDao {

    override fun getWardrobes(): Flow<List<WardrobeEntity>> {
        return flow {
            emit(
                listOf(
                    WardrobeEntity(id = "1", wardrobeName = "Summer Closet",
                        createdAt = "2025-03-01", lastUpdate = "2025-03-07", userId = "user1"),

                    WardrobeEntity(id = "2", wardrobeName = "Winter Closet",
                        createdAt = "2025-03-07", lastUpdate = "2025-03-07", userId = "user1"),

                    WardrobeEntity(id = "2", wardrobeName = "Fall Closet",
                        createdAt = "2025-03-04", lastUpdate = "2025-03-07", userId = "user1")
                )
            )
        }
    }

    override suspend fun insertWardrobe(wardrobe: WardrobeEntity) {
        // No-op for mock, but you can simulate actions if needed
    }

    override suspend fun deleteWardrobe(wardrobe: WardrobeEntity) {
        // No-op for mock
    }
}
