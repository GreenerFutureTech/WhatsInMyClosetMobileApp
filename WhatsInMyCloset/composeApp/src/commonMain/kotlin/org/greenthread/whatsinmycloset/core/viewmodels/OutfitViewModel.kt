package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.data.daos.ClothingItemDao
import kotlinx.datetime.LocalDate
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
    private val clothingItemViewModel: ClothingItemViewModel,
    private val itemDao: ClothingItemDao // Inject ItemDao to fetch items dynamically
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
    fun saveOutfit(selectedTags: List<String>) {
        viewModelScope.launch {
            val currentOutfit = _currentOutfit.value ?: return@launch

            val updatedOutfit = Outfit(
                id = currentOutfit.outfitId,
                name = currentOutfit.name,
                creatorId = currentOutfit.creatorId,
                items = _temporaryPositions.value, // Use the stored positions
                tags = selectedTags,
                calendarDates = _calendarEvents.value.mapNotNull {
                    it.substringBefore(":").trim().let { dateStr ->
                        runCatching { LocalDate.parse(dateStr) }.getOrNull()
                    }
                }
            )

            outfitManager.saveOutfit(updatedOutfit)
            _isOutfitSaved.value = true
            _temporaryPositions.value = emptyMap()
        }
    }

    // when user clicks on "Create New Outfit" button
    // Discard the current outfit and clear selected clothing items
    open fun discardCurrentOutfit() {
        _currentOutfit.value = null
        _clothingItems.value = emptyList() // Clear selected clothing items
        _temporaryPositions.value = emptyMap() // Clear temporary positions
    }

    // Create an outfit using outfit manager
    open fun createOutfit(
        name: String = "",
        selectedItems: List<ClothingItem>,  // Accept list of clothing items
        tags: List<String> = emptyList(),
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                // Combine selected items with their positions from temporaryPositions
                val itemsWithPositions = selectedItems.associate { item ->
                    item.id to (_temporaryPositions.value[item.id] ?: OffsetData(0f, 0f))
                }

                val outfit = outfitManager.createOutfit(
                    name = name,
                    items = itemsWithPositions,  // Pass the map of item IDs to positions
                    tags = tags
                )

                _currentOutfit.value = outfit.toEntity()
                _isCreateNewOutfit.value = true
                _temporaryPositions.value = emptyMap()

                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
                // Log error or show user message
            }
        }
    }

    // Function to update the position of a clothing item
    fun updateClothingItemPosition(itemId: String, newPosition: OffsetData) {
        _temporaryPositions.value = _temporaryPositions.value.toMutableMap().apply {
            put(itemId, newPosition)
        }
    }

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    // Add outfit to calendar
    // TODO Update date in Calendar Entity
    open fun addOutfitToCalendar(dateString: String) {

        val localDate = kotlinx.datetime.LocalDate.parse(dateString)

        _currentOutfit.value?.let { outfit ->
            _calendarEvents.value = _calendarEvents.value + "$localDate: ${outfit.name}"
        }
    }

    // TODO get outfit for the dates for calendar

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

