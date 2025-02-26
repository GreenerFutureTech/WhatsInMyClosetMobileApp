package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository


/* this viewmodel handles the following:
creating new folders (updating the outfit repository)
selectedFolder state
selectedFolders state (when user wants to save outfit in more than 1 folder)
isPublic state (when user wants the outfit to be public)

This ViewModel manages outfit creation, saving, and calendar events
*/
open class OutfitViewModel(savedStateHandle: SavedStateHandle? = null) : ViewModel() {
    private var outfitCounter = 0

    private val selectedCategoryKey = "selected_category"
    private val selectedItemsKey = "selected_items"

    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String>("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categoryItems = MutableStateFlow<List<ClothingCategory>>(emptyList())
    val categoryItems: StateFlow<List<ClothingCategory>> = _categoryItems.asStateFlow()

    private val _calendarEvents = MutableStateFlow<List<String>>(emptyList())
    val calendarEvents: StateFlow<List<String>> = _calendarEvents.asStateFlow()

    // State for OutfitSaveScreen
    private val outfitRepository = OutfitRepository()
    open val outfitFolders: StateFlow<List<String>> = outfitRepository.outfitFolders

    private val _selectedFolder = MutableStateFlow<String?>(null)
    open val selectedFolder: StateFlow<String?> = _selectedFolder.asStateFlow()

    private val _selectedFolders = MutableStateFlow<List<String>>(emptyList())
    open val selectedFolders: StateFlow<List<String>> = _selectedFolders.asStateFlow()

    private val _isPublic = MutableStateFlow(false)
    open val isPublic: StateFlow<Boolean> = _isPublic.asStateFlow()

    private val _isCreateNewOutfit = MutableStateFlow(false)
    open val isCreateNewOutfit: StateFlow<Boolean> = _isCreateNewOutfit.asStateFlow()

    // Add isOutfitSaved state
    private val _isOutfitSaved = MutableStateFlow(false)
    open val isOutfitSaved: StateFlow<Boolean> = _isOutfitSaved.asStateFlow()

    // For tracking the current outfit being created
    private val _currentOutfit = MutableStateFlow<Outfit?>(null)
    val currentOutfit: StateFlow<Outfit?> = _currentOutfit.asStateFlow()

    init {
        savedStateHandle?.let { loadState(it) } // Only call if non-null
    }

    // Load state - ensures that critical state data is preserved during config changes
    private fun loadState(savedStateHandle: SavedStateHandle) {
        _selectedCategory.value = savedStateHandle.get<String>(selectedCategoryKey) ?: ""
        _clothingItems.value = savedStateHandle.get<List<ClothingItem>>(selectedItemsKey) ?: emptyList()
    }

    // Save state - call this function whenever the state changes
    // (e.g., when _selectedCategory or _clothingItems is updated)
    fun saveState(savedStateHandle: SavedStateHandle) {
        savedStateHandle[selectedCategoryKey] = _selectedCategory.value
        savedStateHandle[selectedItemsKey] = _clothingItems.value
    }

    // Update selected folder (single selection)
    open fun updateSelectedFolder(folder: String?) {
        _selectedFolder.value = folder
    }

    // Update selected folders (multiple selection)
    open fun updateSelectedFolders(folder: String) {
        val currentFolders = _selectedFolders.value.toMutableList()
        if (currentFolders.contains(folder)) {
            currentFolders.remove(folder)
        } else {
            currentFolders.add(folder)
        }
        _selectedFolders.value = currentFolders
    }

    // Toggle public state
    open fun toggleIsPublic(isPublic: Boolean) {
        _isPublic.value = isPublic
        if (isPublic) {
            _selectedFolders.value = listOf("My Public Outfits")
        }
    }

    // Add a new folder using outfitRepository class
    open fun addFolder(folderName: String) {
        outfitRepository.addFolder(folderName)
    }

    // Save the outfit and update isOutfitSaved state
    open fun saveOutfit(outfit: Outfit) {
        // Save the outfit to the repository or database
        outfitRepository.saveOutfit(outfit)

        // Update isOutfitSaved state
        _isOutfitSaved.value = true
    }

    private fun generateOutfitId(): String {
        return "outfit_${outfitCounter++}"
    }

    // when user clicks on "Create New Outfit" button
    // Discard the current outfit and clear selected clothing items
    open fun discardCurrentOutfit() {
        _currentOutfit.value = null
        _clothingItems.value = emptyList() // Clear selected clothing items
    }

    // Create an outfit
    open fun createOutfit(clothingItems: List<ClothingItem>) {
        _isCreateNewOutfit.value = true

        _currentOutfit.value = Outfit(
            id = generateOutfitId(), // Generate a unique ID
            name = "New Outfit",
            itemIds = _clothingItems.value // Use the selected clothing items
        )
    }

    // Add outfit to calendar
    open fun addOutfitToCalendar(date: String) {
        val outfit = _currentOutfit.value
        if (outfit != null) {
            _calendarEvents.value = _calendarEvents.value + "$date: ${outfit.name}"
        }
    }
}

