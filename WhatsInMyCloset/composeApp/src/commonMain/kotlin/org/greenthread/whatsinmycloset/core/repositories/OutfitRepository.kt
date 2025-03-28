package org.greenthread.whatsinmycloset.core.repositories

import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.data.daos.OutfitDao
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.isSuccess
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.toDomain
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems

/*
    Handles database operations for outfits, including inserting, deleting, and fetching outfits.

    Acts as an intermediary between the OutfitManager and the DAO layer

*/
open class OutfitRepository(
    private val outfitDao: OutfitDao,
    val remoteSource: KtorRemoteDataSource
) {
    // Add this JSON serializer instance
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // first insert outfit in backend and then add it to room
    suspend fun saveOutfit(outfit: Outfit): String? {
        return try {
            val remoteResult = remoteSource.postOutfitForUser(outfit.toDto())
            println("Server response: $remoteResult")

            if (remoteResult.isSuccess()) {
                val serverOutfit = remoteResult.getOrNull()
                if (serverOutfit != null) {
                    val entity = OutfitEntity(
                        outfitId = serverOutfit.id,
                        name = serverOutfit.name,
                        creatorId = serverOutfit.userId.toInt(),
                        items = json.encodeToString(outfit.items),
                        tags = json.encodeToString(outfit.tags)
                    )
                    outfitDao.insertOutfit(entity)
                    serverOutfit.id // Return the server-generated ID
                } else {
                    null
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
    fun Outfit.toDto(): OutfitDto {
        return OutfitDto(
            name = name,
            itemIds = items.map { (id, offsetData) ->
                OutfitItems(id = id, x = offsetData.x, y = offsetData.y) // Ensure correct field mapping
            },
            userId = creatorId,
            tags = tags,
            id = id
        )
    }

}