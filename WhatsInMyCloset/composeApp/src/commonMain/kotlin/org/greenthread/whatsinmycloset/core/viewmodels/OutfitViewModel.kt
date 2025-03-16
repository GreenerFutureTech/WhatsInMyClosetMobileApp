package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.data.daos.ItemDao
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import org.greenthread.whatsinmycloset.core.managers.OutfitManager
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.persistence.toClothingItem
import org.greenthread.whatsinmycloset.core.repositories.OutfitTags

/*
*   Manages the UI state for outfits, including selected items, tags, and temporary positions.

    Acts as an intermediary between the UI and the OutfitManager
* */
open class OutfitViewModel
    (
    /* The OutfitManager and OutfitTags are injected into the ViewModel
    instead of being instantiated directly */
    private val outfitManager: OutfitManager,
    private val outfitTags: OutfitTags,
    private val itemDao: ItemDao, // Inject ItemDao to fetch items dynamically
    savedStateHandle: SavedStateHandle? = null
    )
    : ViewModel()
{

    // State for clothing items
    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

    // State for selected category
    private val _selectedCategory = MutableStateFlow<String>("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // State for category items
    private val _categoryItems = MutableStateFlow<List<ClothingCategory>>(emptyList())
    val categoryItems: StateFlow<List<ClothingCategory>> = _categoryItems.asStateFlow()

    private val _calendarEvents = MutableStateFlow<List<String>>(emptyList())
    val calendarEvents: StateFlow<List<String>> = _calendarEvents.asStateFlow()

    // State for tags
    private val _tags = MutableStateFlow<Set<String>>(emptySet())
    val tags: StateFlow<Set<String>> = _tags.asStateFlow()

    // State for selected tags
    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    val selectedTags: StateFlow<Set<String>> = _selectedTags.asStateFlow()

    // State for public visibility
    private val _isPublic = MutableStateFlow(false)
    open val isPublic: StateFlow<Boolean> = _isPublic.asStateFlow()

    // State for creating a new outfit
    private val _isCreateNewOutfit = MutableStateFlow(false)
    open val isCreateNewOutfit: StateFlow<Boolean> = _isCreateNewOutfit.asStateFlow()

    // Add isOutfitSaved state
    private val _isOutfitSaved = MutableStateFlow(false)
    open val isOutfitSaved: StateFlow<Boolean> = _isOutfitSaved.asStateFlow()

    // For tracking the current outfit being created
    private val _currentOutfit = MutableStateFlow<OutfitEntity?>(null)  // using data class
    val currentOutfit: StateFlow<OutfitEntity?> = _currentOutfit.asStateFlow()

    // State for temporary positions of clothing items
    private val _temporaryPositions = MutableStateFlow<Map<String, OffsetData>>(emptyMap())
    val temporaryPositions: StateFlow<Map<String, OffsetData>> = _temporaryPositions.asStateFlow()

    init {
        // Initialize tags from OutfitTags
        viewModelScope.launch {
            outfitTags.allTags.collect { tags ->
                _tags.value = tags // Update the tags state when OutfitTags emits new data
            }
        }
    }

    // Update selected folders (multiple selection)
    fun updateSelectedTags(tagName: String) {
        val updatedTags = _selectedTags.value.toMutableSet()
        if (updatedTags.contains(tagName)) {
            updatedTags.remove(tagName) // Remove the tag if it exists
        } else {
            updatedTags.add(tagName) // Add the tag if it doesn't exist
        }
        _selectedTags.value = updatedTags
    }

    // Toggle public state
    open fun toggleIsPublic(isPublic: Boolean) {
        _isPublic.value = isPublic

        if (isPublic)
        {
            updateSelectedTags("Public")
        }
    }

    // Add a new folder using outfitTags class
    open fun addNewTag(tagName: String) {
        outfitTags.addTag(tagName)

        // The tags state will automatically update because we're observing outfitTags.allTags

    }

    // Remove a tag and remove from selected in case it was selected
    fun removeTag(tagName: String) {
        val updatedTags = _selectedTags.value.toMutableSet()
        if (updatedTags.contains(tagName)) {
            updatedTags.remove(tagName) // Remove the tag if it exists
        }
        _selectedTags.value = updatedTags

        outfitTags.removeTag(tagName)
    }

    // Save the outfit and finalize item positions
    fun saveOutfit(outfit: OutfitEntity, selectedTags: List<String>) {
        viewModelScope.launch {
            // Fetch the items dynamically for the outfit using ItemDao
            val itemEntities = itemDao.getItemsForWardrobe("1")

            // Convert ItemEntity to ClothingItem
            val clothingItems = itemEntities.map { it.toClothingItem() }

            // Apply temporary positions to the outfit's items
            val updatedItems = clothingItems.map { item ->
                val temporaryPosition = _temporaryPositions.value[item.id]
                if (temporaryPosition != null) {
                    item.copy(temporaryPosition = temporaryPosition) // Update the item's position
                } else {
                    item
                }
            }

            // Convert OutfitEntity to Outfit using the fetched items
            val outfitDomain = Outfit(
                id = outfit.outfitId,
                userId = outfit.userId,
                name = outfit.name,
                public = outfit.public,
                items = updatedItems,
                favorite = outfit.favorite,
                tags = outfit.tags
            )

            // Delegate saving operation to OutfitManager
            outfitManager.saveOutfit(outfitDomain, selectedTags)

            // Clear temporary positions after saving
            _temporaryPositions.value = emptyMap()

            // Update state
            _isOutfitSaved.value = true
        }
    }

    // when user clicks on "Create New Outfit" button
    // Discard the current outfit and clear selected clothing items
    // Discard the current outfit and clear selected clothing items
    open fun discardCurrentOutfit() {
        _currentOutfit.value = null
        _clothingItems.value = emptyList() // Clear selected clothing items
        _temporaryPositions.value = emptyMap() // Clear temporary positions
    }

    // Create an outfit using outfit manager
    open fun createOutfit(
        clothingItems: List<ClothingItem>,
        name: String = "",
        tags: List<String> = emptyList(),
        isPublic: Boolean = false,
        favourite: Boolean = false
    ) {
        viewModelScope.launch {
            val outfit = outfitManager.createOutfit(
                name = name,
                clothingItems = clothingItems,
                tags = tags,
                isPublic = isPublic,
                favourite = favourite
            )

            // Convert the Outfit (domain model) to OutfitEntity
            val outfitEntity = outfit.toEntity()

            _currentOutfit.value = outfitEntity
            _isCreateNewOutfit.value = true

            // Clear any temporary positions when creating a new outfit
            _temporaryPositions.value = emptyMap()
        }
    }

    // Function to update the position of a clothing item
    fun updateClothingItemPosition(itemId: String, newPosition: OffsetData) {
        val updatedPositions = _temporaryPositions.value.toMutableMap()
        updatedPositions[itemId] = newPosition
        _temporaryPositions.value = updatedPositions
    }

    // Add outfit to calendar
    open fun addOutfitToCalendar(date: String) {
        _currentOutfit.value?.let { outfit ->
            _calendarEvents.value = _calendarEvents.value + "$date: ${outfit.name}"
        }
    }

    open fun clearOutfitState() {
        _currentOutfit.value = null // Clear the current outfit
        _clothingItems.value = emptyList() // Clear selected clothing items
        _selectedTags.value = emptySet() // Clear selected folder
        _calendarEvents.value = emptyList()
        _isPublic.value = false // Reset public state
        _isOutfitSaved.value = false // Reset outfit saved state
    }

    companion object {
        private const val selectedCategoryKey = "selected_category"
        private const val selectedItemsKey = "selected_items"
    }
}

