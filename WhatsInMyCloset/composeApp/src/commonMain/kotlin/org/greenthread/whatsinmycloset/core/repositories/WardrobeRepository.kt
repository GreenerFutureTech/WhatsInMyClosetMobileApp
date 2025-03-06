package org.greenthread.whatsinmycloset.core.repositories
/*
import org.greenthread.whatsinmycloset.core.data.MyClosetDatabase
import org.greenthread.whatsinmycloset.core.data.daos.WardrobeDao
import org.greenthread.whatsinmycloset.core.persistence.ItemEntity
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class WardrobeRepository(private val db: MyClosetDatabase) {
    private val wardrobeDao = db.wardrobeDao()

    suspend fun insertWardrobeWithItems(wardrobe: WardrobeEntity, items: List<ItemEntity>) {
/*        db. {
            db.wardrobeDao().insert(wardrobe) test
            items.forEach { db.itemDao().insert(it) }
        }*/
        wardrobeDao.insert(wardrobe)
    }

    suspend fun getWardrobeWithItems(wardrobeId: String): WardrobeEntity? {
        //return db.wardrobeWithItemsDao().getWardrobeWithItems(wardrobeId)
        return wardrobeDao.getWardrobe(wardrobeId)
    }
}*/