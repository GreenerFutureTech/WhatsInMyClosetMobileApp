package org.greenthread.whatsinmycloset.core.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository

class MockClothingItemViewModel : ClothingItemViewModel() {
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
    account: Account, // Add Account parameter
    initialSelectedFolder: String? = null,
    initialSelectedFolders: List<String> = emptyList(),
    initialIsPublic: Boolean = false
): OutfitViewModel(account)
{

    // Default repository names
    public val defaultRepositories = setOf(
        "Business Casuals",
        "Formals",
        "Casuals",
        "Public Outfits"
    )

    // StateFlow for default repositories
    private val _outfitFolders = MutableStateFlow(defaultRepositories)
    override val outfitFolders: StateFlow<Set<String>> = _outfitFolders

    // StateFlow for user-created repositories
    private val _userRepositories = MutableStateFlow<Set<String>>(emptySet())
    val userRepositories: StateFlow<Set<String>> = _userRepositories

    // Combined list of default and user-created repositories
    val allRepositories: Set<String>
        get() = _outfitFolders.value + _userRepositories.value

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

    /**
     * Add a new user-created repository name.
     */
    fun addUserRepository(name: String) {
        if (name.isNotBlank() && name !in allRepositories) {
            _userRepositories.value = _userRepositories.value + name
        }
    }
}