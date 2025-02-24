package org.greenthread.whatsinmycloset.core.repository

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.OtherUserSwapDto
import org.greenthread.whatsinmycloset.core.network.RemoteSwapDataSource
import org.greenthread.whatsinmycloset.core.dto.SwapDto

class DefaultSwapRepository(
    private val remoteSwapDataSource: RemoteSwapDataSource
): SwapRepository {
    override suspend fun getSwaps(userId: String): Result<List<SwapDto>, DataError.Remote> {
        return remoteSwapDataSource.getSwaps(userId)
    }

    override suspend fun getOtherUsersSwaps(currentUserId: String): Result<List<OtherUserSwapDto>, DataError.Remote> {
        return remoteSwapDataSource.getOtherUsersSwaps(currentUserId)
    }

    override suspend fun getAllSwaps(): Result<List<SwapDto>, DataError.Remote> {
        return remoteSwapDataSource.getAllSwaps()
    }

//    override suspend fun getUser(userEmail: String): Result<UserDto, DataError.Remote> {
//       return remoteSwapDataSource.getUser(userEmail)
//    }
}

