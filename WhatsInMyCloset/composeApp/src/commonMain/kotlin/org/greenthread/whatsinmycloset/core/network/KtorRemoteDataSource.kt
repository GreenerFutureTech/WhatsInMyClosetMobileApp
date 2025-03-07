package org.greenthread.whatsinmycloset.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.greenthread.whatsinmycloset.core.data.safeCall
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.getPlatform

private val platform = getPlatform()

private val BASE_URL =  if (platform.name == "iOS") "http://127.0.0.1:13000" else "http://10.0.2.2:13000"
//"https://green-api-c9h6f7huhuezbuhv.eastus2-01.azurewebsites.net"

class KtorRemoteDataSource(
    private val httpClient: HttpClient
): RemoteClosetDataSource {
    //============================= Swap ==================================
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

    //============================= Messages  =================================
    override suspend fun getLatestMessage(userId: String): Result<List<MessageDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/messages/latest/$userId"
            )
        }
    }

    override suspend fun getChatHistory(userId: Int, otherUserId: String): Result<List<MessageDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/messages/chat/$userId/$otherUserId"
            )
        }
    }

    //============================= User ==================================
    override suspend fun createUser(user: UserDto): Result<UserDto, DataError.Remote> {
        return safeCall {
            httpClient.post(
                urlString = "$BASE_URL/users"
            ) {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
        }
    }

    override suspend fun getUser(userEmail: String): Result<UserDto, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/users/email/${userEmail}"
            )
        }
    }

    override suspend fun getUserById(userId: Int): Result<UserDto, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/users/email/${userId}"
            )
        }
    }


    override suspend fun updateUser(user: UserDto): Result<UserDto, DataError.Remote> {
        return safeCall {
            httpClient.put(
                urlString = "$BASE_URL/users/${user.id}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
        }
    }
}
