package org.greenthread.whatsinmycloset.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.greenthread.whatsinmycloset.core.data.safeCall
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.getPlatform

private val platform = getPlatform()
private val BASE_URL = if (platform.name == "iOS") "http://127.0.0.1:13000" else "http://10.0.2.2:13000"


class KtorRemoteDataSource(
    private val httpClient: HttpClient
): RemoteSwapDataSource {

    override suspend fun getSwaps(userId: String): Result<List<SwapDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/swaps/user/$userId"
            )
        }
    }

    override suspend fun getOtherUsersSwaps(currentUserId: String): Result<List<SwapDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/swaps/others/$currentUserId"
            )
        }
    }

    override suspend fun getAllSwaps(): Result<List<SwapDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/swaps"
            )
        }
    }

//    override suspend fun getUser(userEmail: String): Result<UserDto, DataError.Remote> {
//        return safeCall {
//            httpClient.get(
//                urlString = "$BASE_URL/user/$userEmail"
//            )
//        }
//    }
}
