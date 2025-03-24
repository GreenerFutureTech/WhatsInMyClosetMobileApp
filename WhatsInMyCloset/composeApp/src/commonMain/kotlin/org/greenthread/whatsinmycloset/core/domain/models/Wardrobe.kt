package org.greenthread.whatsinmycloset.core.ui.components.models

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.User

class Wardrobe(wardrobeName: String, id: String, createdAt: String, lastUpdate: String, user: User) {
    val id = id
    val items = mutableMapOf<String, ClothingItem>() // Maps item ID to ClothingItem
    val wardrobeName = wardrobeName
    val createdAt = ""
    val lastUpdate = ""
    val user = user

    override fun toString(): String {
        return "${wardrobeName}, ${items.count()} items, createdAt: ${createdAt}, " +
                "lastUpdate: ${lastUpdate}, user: ${user}, serverId: ${id}"
    }

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