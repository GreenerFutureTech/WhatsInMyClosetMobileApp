package org.greenthread.whatsinmycloset.core.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

open class WardrobeManager(
    private val wardrobeRepository: WardrobeRepository,
    private val userManager: UserManager
) {
    var cachedWardrobes = emptyList<Wardrobe>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getWardrobes()
            println("GreenThread Inside WardrobeManager.init (Did not refresh cache)")
        }
    }

    suspend fun getWardrobes() {
        if (cachedWardrobes.isEmpty()){
            cachedWardrobes = getWardrobesFromDB() // Collect Flow once
            println("GreenThread checking for wardrobes in Room")
        }
        if (cachedWardrobes.isEmpty()){
            val cachedWardrobesResult = getWardobesRemote(userManager.currentUser.value?.id.toString()) // Collect Flow once
            println("GreenThread wardrobes not found in room, collecting from server.")
            // Extract the list of wardrobes from the result
            cachedWardrobes = cachedWardrobesResult.getOrNull() ?: emptyList()
            // You can also log the error in case of failure
            cachedWardrobesResult.onError {
                println("Error retrieving wardrobes from remote: $it")
            }
        }
    }

    suspend fun getWardrobesFromDB(): List<Wardrobe> {
        return withContext(Dispatchers.IO) {
            wardrobeRepository.getWardrobes().first()
        }
    }

    suspend fun getWardobesRemote(userId: String): Result<List<Wardrobe>, DataError.Remote> {
        return withContext(Dispatchers.IO) {
            wardrobeRepository.getWardrobesRemote(userId)
        }
    }

    suspend fun insertWardrobe(wardrobe: WardrobeEntity) {
        wardrobeRepository.insertWardrobe((wardrobe))
    }
}