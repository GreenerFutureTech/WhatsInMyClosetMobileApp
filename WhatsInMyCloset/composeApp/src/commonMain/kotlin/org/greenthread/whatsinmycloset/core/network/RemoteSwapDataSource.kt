package org.greenthread.whatsinmycloset.core.network

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.domain.Result

interface RemoteSwapDataSource {
    suspend fun getSwaps(userId: String): Result<List<SwapDto>, DataError.Remote>
}
