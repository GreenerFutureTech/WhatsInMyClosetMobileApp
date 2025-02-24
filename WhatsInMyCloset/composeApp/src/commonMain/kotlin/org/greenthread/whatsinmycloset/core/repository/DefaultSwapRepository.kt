package org.greenthread.whatsinmycloset.core.repository

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.network.RemoteSwapDataSource
import org.greenthread.whatsinmycloset.core.dto.SwapDto

class DefaultSwapRepository(
    private val remoteSwapDataSource: RemoteSwapDataSource
): SwapRepository {
    override suspend fun getSwaps(userId: String): Result<List<SwapDto>, DataError.Remote> { // ✅ List<SwapDto> 반환하도록 수정
        return remoteSwapDataSource.getSwaps(userId)
    }
}

