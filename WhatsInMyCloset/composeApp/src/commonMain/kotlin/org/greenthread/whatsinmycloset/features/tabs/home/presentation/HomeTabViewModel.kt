package org.greenthread.whatsinmycloset.features.tabs.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.features.tabs.home.domain.HomeTabAction

class HomeTabViewModel(
    private val wardrobeRepository: WardrobeRepository,
    private val wardrobeManager: WardrobeManager
) : ViewModel() {

    private var cachedWardrobes = wardrobeManager.cachedWardrobes
    var defaultWardrobe: Wardrobe? = cachedWardrobes.firstOrNull()

    private val _state = MutableStateFlow(HomeTabState())
    val state = _state
        .onStart {

        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    fun testDb(){
        // Sample wardrobe entity
        val sampleWardrobe = WardrobeEntity(
            id = "123",
            wardrobeName = "Winter Collection",
            createdAt = "2024-03-05",
            lastUpdate = "2024-03-05",
            userId = "user_001"
        )

        // Insert sample data
        CoroutineScope(Dispatchers.IO).launch {
            wardrobeRepository.insertWardrobe(sampleWardrobe)
            // Retrieve and print wardrobes

            val wardrobes = wardrobeRepository.getWardrobes() // Collect Flow once
            val wardrobeList = wardrobes.firstOrNull()
            if (wardrobeList?.isNotEmpty() == true) {
                defaultWardrobe = wardrobeList.get(0) // Assign the first wardrobe
            }
            println("GreenThread Ran testDb from HomeTabViewModel to insert a wardrobe into the DB and retrieve it: ${defaultWardrobe.toString()}")
        }
    }


    fun onAction(action: HomeTabAction) {
        when (action) {
            is HomeTabAction.OnViewWardrobeItems -> {
                _state.update {
                    it.copy(selectedWardrobeIndex = action.index)
                }
            }

            is HomeTabAction.OnSetWardrobe -> {
                _state.update {
                    it.copy(selectedWardrobeIndex = action.index)
                }
            }

            is HomeTabAction.OnAddNewItem -> {
                _state.update {
                    it.copy(selectedWardrobeIndex = action.index)
                }
            }

            is HomeTabAction.CreateNewOutfit -> {
                _state.update {
                    it.copy(selectedWardrobeIndex = action.index)
                }
            }
        }
    }
}

data class HomeTabState(
    val isLoading: Boolean = true,
    val selectedWardrobeIndex: Int = 0,
)