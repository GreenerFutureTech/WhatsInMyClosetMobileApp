package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.greenthread.whatsinmycloset.core.data.daos.ItemDao
import org.greenthread.whatsinmycloset.core.data.daos.OutfitDao
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.EmptyResult
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitItemJoin
import org.greenthread.whatsinmycloset.core.persistence.toOutfit

/*
    Handles database operations for outfits, including inserting, deleting, and fetching outfits.

    Acts as an intermediary between the OutfitManager and the DAO layer

*/
open class OutfitRepository(
    private val outfitDao: OutfitDao,
    private val itemDao: ItemDao
) {
    suspend fun insertOutfit(outfit: OutfitEntity): EmptyResult<DataError.Local> {
        return try {
            outfitDao.insertOutfit(outfit)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    suspend fun deleteOutfit(outfit: OutfitEntity) {
        outfitDao.deleteOutfit(outfit.outfitId) // Delegate to OutfitDao
    }

    // Insert a relationship between an outfit and a clothing item
    suspend fun insertOutfitItemJoin(join: OutfitItemJoin) {
        outfitDao.insertOutfitItemJoin(join) // Delegate to OutfitDao
    }

    // get a specific outfit by ID
    suspend fun getOutfitById(outfitId: String, userId: Int): OutfitEntity? {
        val outfits = outfitDao.getOutfits(userId).first() // Collect the first emission of the Flow

        return outfits.find { it.outfitId == outfitId } // Find the outfit by ID
    }

    // get a specific outfit by outfit name
    suspend fun getOutfitByName(outfitName: String, userId: Int): OutfitEntity? {
        val outfits = outfitDao.getOutfits(userId).first() // Collect the first emission of the Flow
        return outfits.find { it.name == outfitName } // Find the outfit by name
    }

    // get all outfits
    open fun getOutfits(userId: Int): Flow<List<Outfit>> {
        return outfitDao.getOutfits(userId)
            .map { outfitEntities ->
                outfitEntities.map { it.toOutfit(itemDao) } // Convert entities to domain models
            }
    }
}