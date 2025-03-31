package org.greenthread.whatsinmycloset.core.repositories

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.greenthread.whatsinmycloset.core.data.daos.OutfitDao
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.EmptyResult
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.isSuccess
import org.greenthread.whatsinmycloset.core.domain.map
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.dto.toOutfit
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

open class OutfitRepository(
    private val outfitDao: OutfitDao,
    val remoteSource: KtorRemoteDataSource
) {

    suspend fun insertOutfit(outfit: OutfitEntity): EmptyResult<DataError.Local>  {
        return try {
            outfitDao.insertOutfit(outfit)
            Result.Success(Unit)
        } catch(e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    suspend fun insertOutfits(outfits: List<OutfitEntity>) {
        outfits.forEach { outfitDao.insertOutfit(it) }
    }

    suspend fun saveOutfit(outfit: Outfit): OutfitDto? {
        return try {
            val remoteResult = remoteSource.postOutfitForUser(outfit.toDto())
            println("Server response: $remoteResult")

            if (remoteResult.isSuccess()) {
                remoteResult.getOrNull()?.also { dto ->
                    outfitDao.insertOutfit(OutfitEntity.fromDto(dto))
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteOutfit(outfit: OutfitEntity) {
        outfitDao.deleteOutfit(outfit.id)
    }

    fun getOutfits(): Flow<List<Outfit>> {
        return outfitDao
            .getOutfits()
            .map { outfitEntities ->
                outfitEntities.map { it.toDto().toOutfit() }
            }
    }

    suspend fun getOutfitsRemote(userId: String): Result<List<Outfit>, DataError.Remote> {
        return remoteSource
            .getAllOutfitsForUser(userId.toInt())
            .map { dto ->
                dto.map { it.toOutfit() }
            }
    }

}