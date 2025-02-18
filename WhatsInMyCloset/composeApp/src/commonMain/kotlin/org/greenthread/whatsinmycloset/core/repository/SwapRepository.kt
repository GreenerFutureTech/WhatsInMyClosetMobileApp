package org.greenthread.whatsinmycloset.core.repository

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.features.tabs.swap.dto.SwapDto

interface SwapRepository {
    suspend fun getSwaps(userId: String) : Result<List<SwapDto>,DataError.Remote>
}