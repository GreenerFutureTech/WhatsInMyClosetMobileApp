package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import org.greenthread.whatsinmycloset.core.managers.OutfitManager
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.models.toEntity
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.repositories.OutfitTags
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

/*
*   Manages the UI state for outfits, including selected items, tags, and temporary positions.

    Acts as an intermediary between the UI and the OutfitManager
* */
open class OutfitViewModel(
    private val outfitManager: OutfitManager,
    private val outfitTags: OutfitTags,
    private val wardrobeManager: WardrobeManager,
    private val userManager: UserManager
) : ViewModel() {

    // Source data flows
    val cachedWardrobes: StateFlow<List<Wardrobe>> = wardrobeManager.cachedWardrobes
    private val _cachedItems = wardrobeManager.cachedItems
    val currentUser = userManager.currentUser

    // Filtered items state
    private val _filteredItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val filteredItems: StateFlow<List<ClothingItem>> = _filteredItems.asStateFlow()

    // Filter states
    private val _selectedCategory = mutableStateOf<String?>(null)

    val selectedWardrobe = mutableStateOf<String?>(null)

    // UI states
    private val _calendarEvents = MutableStateFlow<List<String>>(emptyList())
    val calendarEvents: StateFlow<List<String>> = _calendarEvents.asStateFlow()

    private val _tags = MutableStateFlow<Set<String>>(emptySet())
    val tags: StateFlow<Set<String>> = _tags.asStateFlow()

    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    val selectedTags: StateFlow<Set<String>> = _selectedTags.asStateFlow()

    private val _isPublic = MutableStateFlow(false)
    val isPublic: StateFlow<Boolean> = _isPublic.asStateFlow()

    private val _isCreateNewOutfit = MutableStateFlow(false)
    val isCreateNewOutfit: StateFlow<Boolean> = _isCreateNewOutfit.asStateFlow()

    private val _isOutfitSaved = MutableStateFlow(false)
    val isOutfitSaved: StateFlow<Boolean> = _isOutfitSaved.asStateFlow()

    private val _currentOutfit = MutableStateFlow<OutfitEntity?>(null)
    val currentOutfit: StateFlow<OutfitEntity?> = _currentOutfit.asStateFlow()

    private val _temporaryPositions = MutableStateFlow<Map<String, OffsetData>>(emptyMap())
    val temporaryPositions: StateFlow<Map<String, OffsetData>> = _temporaryPositions.asStateFlow()

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    init {
        // Initialize tags
        viewModelScope.launch {
            outfitTags.allTags.collect { tags ->
                _tags.value = tags
            }
        }

        // Initialize wardrobe selection with first available wardrobe
        viewModelScope.launch {
            cachedWardrobes.collectLatest { wardrobes ->
                if (wardrobes.isNotEmpty() && selectedWardrobe.value == null) {
                    selectedWardrobe.value = wardrobes.first().id
                }
                // Apply filters whenever wardrobes or items change
                applyFilters(_cachedItems.value)
            }
        }

        // Observe items and apply filters when they change
        viewModelScope.launch {
            _cachedItems.collectLatest { items ->
                applyFilters(items)
            }
        }
    }

    // Filtering functions
    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
        applyFilters(_cachedItems.value)
    }

    fun setWardrobeFilter(wardrobeId: String?) {
        selectedWardrobe.value = wardrobeId
        applyFilters(_cachedItems.value)
    }

    private fun applyFilters(items: List<ClothingItem>) {
        _filteredItems.value = items
            .filter { item ->
                _selectedCategory.value?.let { selectedCategory ->
                    try {
                        // Convert the item's string category to enum for comparison
                        val itemCategory = ClothingCategory.fromString(selectedCategory)
                        itemCategory == item.itemType
                    } catch (e: IllegalArgumentException) {
                        false // Skip items with unknown categories
                    }
                } ?: true // No category filter selected, include all items
            }
            .filter { item ->
                selectedWardrobe.value?.let { wardrobeId ->
                    item.wardrobeId == wardrobeId
                } ?: true
            }
    }

    fun getItemsByWardrobe(wardrobeId: String): List<ClothingItem> {
        return _cachedItems.value.filter { it.wardrobeId == wardrobeId }
    }

    // Tag management
    fun updateSelectedTags(tagName: String) {
        _selectedTags.value = _selectedTags.value.toMutableSet().apply {
            if (contains(tagName)) remove(tagName) else add(tagName)
        }
    }

    fun addNewTag(tagName: String) {
        outfitTags.addTag(tagName)
    }

    fun removeTag(tagName: String) {
        _selectedTags.value = _selectedTags.value - tagName
        outfitTags.removeTag(tagName)
    }

    // Outfit creation and management
    fun saveOutfit(selectedTags: List<String>) {
        viewModelScope.launch {
            val currentOutfit = _currentOutfit.value ?: return@launch

            val updatedOutfit = Outfit(
                id = currentOutfit.outfitId,
                name = currentOutfit.name,
                creatorId = currentOutfit.creatorId,
                items = _temporaryPositions.value,
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

    fun discardCurrentOutfit() {
        _currentOutfit.value = null
        _temporaryPositions.value = emptyMap()
    }

    fun createOutfit(
        name: String = "",
        selectedItems: List<ClothingItem>,
        tags: List<String> = emptyList(),
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                val itemsWithPositions = selectedItems.associate { item ->
                    item.id to (_temporaryPositions.value[item.id] ?: OffsetData(0f, 0f))
                }

                val outfit = outfitManager.createOutfit(
                    name = name,
                    items = itemsWithPositions,
                    tags = tags
                )

                _currentOutfit.value = outfit.toEntity()
                _isCreateNewOutfit.value = true
                _temporaryPositions.value = emptyMap()
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    // Item position management
    fun updateClothingItemPosition(itemId: String, newPosition: OffsetData) {
        _temporaryPositions.value = _temporaryPositions.value.toMutableMap().apply {
            put(itemId, newPosition)
        }
    }

    // Calendar functions
    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun addOutfitToCalendar(dateString: String) {
        val localDate = LocalDate.parse(dateString)
        _currentOutfit.value?.let { outfit ->
            _calendarEvents.value = _calendarEvents.value + "$localDate: ${outfit.name}"
        }
    }

    fun clearOutfitState() {
        _currentOutfit.value = null
        _selectedTags.value = emptySet()
        _calendarEvents.value = emptyList()
        _isPublic.value = false
        _isOutfitSaved.value = false
    }

    // Public visibility toggle
    fun toggleIsPublic(isPublic: Boolean) {
        _isPublic.value = isPublic
        if (isPublic) {
            updateSelectedTags("Public")
        }
    }

    companion object {
        private const val selectedCategoryKey = "selected_category"
        private const val selectedItemsKey = "selected_items"
    }
}

