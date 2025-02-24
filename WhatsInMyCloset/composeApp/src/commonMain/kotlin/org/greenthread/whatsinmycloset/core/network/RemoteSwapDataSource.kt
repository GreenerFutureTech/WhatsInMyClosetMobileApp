package org.greenthread.whatsinmycloset.core.network

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.OtherUserSwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto

interface RemoteSwapDataSource {
    suspend fun getSwaps(userId: String): Result<List<SwapDto>, DataError.Remote>
    suspend fun getOtherUsersSwaps(currentUserId: String): Result<List<OtherUserSwapDto>, DataError.Remote>
    suspend fun getAllSwaps(): Result<List<SwapDto>, DataError.Remote>
   // suspend fun getUser(userEmail: String): Result<UserDto, DataError.Remote>
}
