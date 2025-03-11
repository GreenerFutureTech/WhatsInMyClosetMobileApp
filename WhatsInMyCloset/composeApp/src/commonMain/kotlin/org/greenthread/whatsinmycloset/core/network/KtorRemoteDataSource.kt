package org.greenthread.whatsinmycloset.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.data.safeCall
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.SendMessageRequest
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.getPlatform

private val platform = getPlatform()
//private val BASE_URL = if (platform.name == "iOS") "http://127.0.0.1:13000" else "http://10.0.2.2:13000"
private val BASE_URL = "https://green-api-c9h6f7huhuezbuhv.eastus2-01.azurewebsites.net"


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

    override suspend fun updateStatus(itemId: String): Result<SwapStatusDto, DataError.Remote> {
        return safeCall {
            httpClient.patch(
                urlString = "$BASE_URL/swaps/$itemId"
            ) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("status" to "completed"))
            }
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

    override suspend fun getChatHistory(userId: Int, otherUserId: Int): Result<List<MessageDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/messages/chat/$userId/$otherUserId"
            )
        }
    }

    override suspend fun updateRead(messageId: Int): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.patch(
                urlString = "$BASE_URL/messages/$messageId/read"
            )
        }
    }

    override suspend fun sendMessage(senderId: Int, receiverId: Int, content: String): Result<MessageDto, DataError.Remote> {
        return safeCall {
            val request = SendMessageRequest(
                senderId = senderId,
                receiverId = receiverId,
                content = content
            )

            val jsonRequest = Json.encodeToString(request)
            println("SEND MESSAGE REQUEST ${jsonRequest}")

            httpClient.post(
                urlString = "$BASE_URL/messages"
            ) {
                contentType(ContentType.Application.Json)
                setBody(jsonRequest)
            }
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
                urlString = "$BASE_URL/users/${userId}"
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
