package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.greenthread.whatsinmycloset.core.data.daos.OutfitDao
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.EmptyResult
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.isSuccess
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.toDomain
import org.greenthread.whatsinmycloset.core.domain.models.toEntity
import org.greenthread.whatsinmycloset.core.dto.OffsetDataDto
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

/*
    Handles database operations for outfits, including inserting, deleting, and fetching outfits.

    Acts as an intermediary between the OutfitManager and the DAO layer

*/
open class OutfitRepository(
    private val outfitDao: OutfitDao,
    val remoteSource: KtorRemoteDataSource
) {
    // first insert outfit in backend and then add it to room
    suspend fun saveOutfit(outfit: Outfit): Boolean {
        return try {
            // 1. First save to backend
            val remoteResult = remoteSource.postOutfitForUser(outfit.toDto())

            // 2. If successful, save to local database
            if (remoteResult.isSuccess()) {
                outfitDao.insertOutfit(outfit.toEntity())
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
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

    // Add this conversion function to your Outfit class
    private fun Outfit.toDto(): OutfitDto = OutfitDto(
        id = this.id,
        name = this.name,
        creatorId = this.creatorId,
        items = this.items.mapValues { (_, offset) ->
            OffsetDataDto(offset.x, offset.y)
        },
        tags = this.tags,
        calendarDates = this.calendarDates.map { it.toString() },
        createdAt = this.createdAt
    )

    // Add to your OffsetData class
    private fun OffsetData.toDto() = OffsetDataDto(x, y)

}