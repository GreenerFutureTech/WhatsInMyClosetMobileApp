package org.greenthread.whatsinmycloset.core.ui.components.models

import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

class Wardrobe(nameIn: String, idIn: String) {
    val id = idIn
    val items = mutableMapOf<String, ClothingItem>() // Maps item ID to ClothingItem
    val name = nameIn

    fun addItem(item: ClothingItem) {
        items[item.id] = item
    }

    fun removeItem(itemId: String) {
        items.remove(itemId)
    }

    fun getItem(itemId: String): ClothingItem? {
        return items[itemId]
    }

    fun getAllItems(): List<ClothingItem> {
        return items.values.toList()
    }
}