package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.format.DateTimeFormat
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.repositories.OutfitTags


/* this viewmodel handles the following:
creating new folders (updating the outfit repository)
selectedFolder state
selectedFolders state (when user wants to save outfit in more than 1 folder)
isPublic state (when user wants the outfit to be public)

This ViewModel manages outfit creation, saving, and calendar events
*/

open class OutfitViewModel
    (
    private val user: User, // Pass the logged-in user's account
    savedStateHandle: SavedStateHandle? = null
    )
    : ViewModel()
{
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
    private val outfitTag = OutfitTags(user)
    open val outfitTags: StateFlow<Set<String>> = outfitTag.outfitTags

    private val _tags = MutableStateFlow<Set<String>>(emptySet())
    open val tags: StateFlow<Set<String>> = _tags.asStateFlow()

    private val _isPublic = MutableStateFlow(false)
    open val isPublic: StateFlow<Boolean> = _isPublic.asStateFlow()

    private val _isCreateNewOutfit = MutableStateFlow(false)
    open val isCreateNewOutfit: StateFlow<Boolean> = _isCreateNewOutfit.asStateFlow()

    // Add isOutfitSaved state
    private val _isOutfitSaved = MutableStateFlow(false)
    open val isOutfitSaved: StateFlow<Boolean> = _isOutfitSaved.asStateFlow()

    // For tracking the current outfit being created
    private val _currentOutfit = MutableStateFlow<Outfit?>(null)  // current outfit user wants to save
    val currentOutfit: StateFlow<Outfit?> = _currentOutfit.asStateFlow()

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

    // Update selected folders (multiple selection)
    open fun updateSelectedTags(tagName: String) {
        val currentTags = _tags.value.toMutableSet()
        if (currentTags.contains(tagName)) {
            currentTags.remove(tagName) // Remove the tag if it exists
        } else {
            currentTags.add(tagName) // Add the tag if it doesn't exist
        }
        _tags.value = currentTags
    }

    // Toggle public state
    open fun toggleIsPublic(isPublic: Boolean) {
        _isPublic.value = isPublic

        if (isPublic)
        {
            val currentTags = _tags.value.toMutableSet()
            if (currentTags.contains("Public")) {
                currentTags.remove("Public") // Remove the tag if it exists
            } else {
                currentTags.add("Public") // Add the tag if it doesn't exist
            }

            _tags.value = currentTags
        }
    }

    // Add a new folder using outfitRepository class
    open fun addTag(tagName: String) {
        outfitTag.updateTags(tagName)
    }

    // ADD OUTFIT COORDINATES - SO HOWEVER USER SAVES THE OUTFIT,
    // THE NEXT TIME USER WANTS TO VIEW THE OUTFIT
    // IT CAN SAME COORDINATES
    // Save the outfit and update isOutfitSaved state
    open fun saveOutfit(outfit: Outfit, selectedTags: List<String>? = null,
                        selectedFolder: String?) {
        // Save the outfit to the selected folder
        if (selectedTags != null || selectedFolder != null) {
            if(selectedTags != null)
            {
                outfitTag.saveOutfit(outfit, selectedTags)
            }
        }


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
    open fun createOutfit(clothingItems: List<ClothingItem>)
    {
        _isCreateNewOutfit.value = true
        val outfitId = "outfit_${user.outfitCount() + 1}"

        // Create the outfit with the logged-in user's account
        _currentOutfit.value = Outfit(
            id = outfitId,
            userId = user.retrieveUserId().toString(),
            public = true,
            favorite = true,
            mediaURL = "",
            name = "Summer Look",
            items = clothingItems, // Use the passed clothing items
            createdAt = DateTimeFormat.toString()
        )
    }

    // Function to update the position of a clothing item
    fun updateClothingItemPosition(itemId: String, newPosition: OffsetData) {
        val currentOutfit = _currentOutfit.value
        if (currentOutfit != null) {
            val updatedItems = currentOutfit.items.map { item ->
                if (item.id == itemId) {
                    item.copy(position = newPosition)
                } else {
                    item
                }
            }
            _currentOutfit.value = currentOutfit.copy(items = updatedItems)
        }
    }

    // Add outfit to calendar
    open fun addOutfitToCalendar(date: String) {
        val outfit = _currentOutfit.value
        if (outfit != null) {
            _calendarEvents.value = _calendarEvents.value + "$date: ${outfit.name}"
        }
    }

    open fun clearOutfitState() {
        _currentOutfit.value = null // Clear the current outfit
        _clothingItems.value = emptyList() // Clear selected clothing items
        _tags.value = emptySet() // Clear selected folder
        _calendarEvents.value = emptyList()
        _isPublic.value = false // Reset public state
        _isOutfitSaved.value = false // Reset outfit saved state
    }
}

