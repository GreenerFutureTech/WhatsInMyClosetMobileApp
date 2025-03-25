package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.greenthread.whatsinmycloset.core.data.daos.OutfitDao
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.EmptyResult
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.toDomain
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

/*
    Handles database operations for outfits, including inserting, deleting, and fetching outfits.

    Acts as an intermediary between the OutfitManager and the DAO layer

*/
open class OutfitRepository(
    private val outfitDao: OutfitDao
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

    // TODO Test below

    suspend fun getOutfitByName(userId: Int, outfitName: String): Outfit? {
        return try {
            outfitDao.getOutfitsByOutfitName(userId, outfitName)
                .first()
                .firstOrNull { it.name == outfitName }
                ?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    // get all outfits from current user
    open fun getOutfits(userId: Int): Flow<List<Outfit>> {
        return outfitDao.getOutfitsByUserId(userId)
            .map { entities -> entities.map { it.toDomain() } }
    }

}