package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.User

enum class DefaultTags(val tagName: String)
{
    BUSINESS_CASUAL("Business Casuals"),
    FORMALS("Formals"),
    CASUALS("Casuals"),
    PUBLIC("Public");

    companion object {
        // Extract all tag names as a set of strings
        fun getAllTagNames(): Set<String> {
            return values().map { it.tagName }.toSet()
        }
    }
}

/* This class focuses on managing tags */
class OutfitTags (
    private val user: User
) // Pass the logged-in user's account)
{
    // StateFlow for all tags
    private val _allTags = MutableStateFlow(DefaultTags.getAllTagNames())
    val allTags: StateFlow<Set<String>> get() = _allTags

    /**
     * Add a new user-created tag.
     */
    fun addTag(tagName: String) {
        val updatedTags = _allTags.value.toMutableSet().apply {
            add(tagName)
        }
        _allTags.value = updatedTags // Emit the updated list of tags
    }

    /**
     * Remove a tag
     */
    fun removeTag(tagName: String) {
        val updatedTags = _allTags.value.toMutableSet().apply {
            remove(tagName)
        }
        _allTags.value = updatedTags // Emit the updated list of tags
    }

    /**
     * Update the tags with a new set of tags.
     * This method is used to sync tags when saving an outfit.
     */
    fun updateTags(newTags: Set<String>) {
        val updatedTags = _allTags.value.toMutableSet().apply {
            addAll(newTags) // Add all new tags to the existing set
        }
        _allTags.value = updatedTags // Emit the updated list of tags
    }
}