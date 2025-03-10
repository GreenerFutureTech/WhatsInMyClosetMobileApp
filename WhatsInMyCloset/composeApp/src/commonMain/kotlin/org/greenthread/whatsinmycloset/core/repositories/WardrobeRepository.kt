package org.greenthread.whatsinmycloset.core.repositories

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.greenthread.whatsinmycloset.core.data.MyClosetDatabase
import org.greenthread.whatsinmycloset.core.data.daos.ItemDao
import org.greenthread.whatsinmycloset.core.data.daos.WardrobeDao
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.EmptyResult
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.persistence.toWardrobe
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class WardrobeRepository(
    private val wardrobeDao: WardrobeDao,
    private val itemDao: ItemDao
) {

    suspend fun insertWardrobe(wardrobe: WardrobeEntity): EmptyResult<DataError.Local>  {
        return try {
            wardrobeDao.insertWardrobe(wardrobe)
            Result.Success(Unit)
        } catch(e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    fun getWardrobes(): Flow<List<Wardrobe>> {
        return wardrobeDao
            .getWardrobes()
            .map { wardrobeEntities ->
                wardrobeEntities.map { it.toWardrobe() }
            }
    }
}