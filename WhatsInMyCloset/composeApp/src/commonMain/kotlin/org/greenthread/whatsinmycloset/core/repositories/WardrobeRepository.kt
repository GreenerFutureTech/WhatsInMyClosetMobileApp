package org.greenthread.whatsinmycloset.core.repositories

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.greenthread.whatsinmycloset.core.data.daos.ClothingItemDao
import org.greenthread.whatsinmycloset.core.data.daos.WardrobeDao
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.EmptyResult
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.domain.map
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.dto.toClothingItem
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.persistence.ClothingItemEntity
import org.greenthread.whatsinmycloset.core.persistence.toItem
import org.greenthread.whatsinmycloset.core.persistence.toWardrobe
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class WardrobeRepository(
    val wardrobeDao: WardrobeDao,
    val clothingItemDao: ClothingItemDao,
    val remoteSource: KtorRemoteDataSource
) {

    suspend fun insertWardrobe(wardrobe: WardrobeEntity): EmptyResult<DataError.Local>  {
        return try {
            wardrobeDao.insertWardrobe(wardrobe)
            Result.Success(Unit)
        } catch(e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    fun getWardrobes(userId:Int): Flow<List<Wardrobe>> {
        return wardrobeDao
            .getWardrobes(userId)
            .map { wardrobeEntities ->
                wardrobeEntities.map { it.toWardrobe() }
            }
    }

    suspend fun getWardrobesRemote(userId: String): Result<List<Wardrobe>, DataError.Remote> {
        return remoteSource
            .getAllWardrobesForUser(userId)
            .map { wardrobeEntities ->
                wardrobeEntities.map { it.toWardrobe() }
            }
    }

    suspend fun insertItem(item: ClothingItemEntity): EmptyResult<DataError.Local>  {
        return try {
            clothingItemDao.insert(item)
            Result.Success(Unit)
        } catch(e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    suspend fun getItems(): Flow<List<ClothingItem>> {
        return clothingItemDao
            .getItems()
            .map { itemEntities ->
                itemEntities.map { it.toItem() }
            }
    }

    suspend fun getItemsRemote(userId: String): Result<List<ClothingItem>, DataError.Remote> {
        return remoteSource
            .getAllItemsForUser(userId)
            .map { itemEntities ->
                itemEntities.map { it.toClothingItem() }
            }
    }
}