package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategorySelectionViewModel : ViewModel() {
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
}
