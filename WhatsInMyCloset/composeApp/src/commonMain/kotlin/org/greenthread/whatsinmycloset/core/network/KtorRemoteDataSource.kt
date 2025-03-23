package org.greenthread.whatsinmycloset.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.client.statement.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.data.safeCall
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.CreateSwapRequestDto
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.SendMessageRequest
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.SwapStatusDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification
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

    override suspend fun createSwap(swap: CreateSwapRequestDto): Result<CreateSwapRequestDto, DataError.Remote> {
        return safeCall {
            println("CREATE SWAP REQUEST : ${swap}")
            httpClient.post(
                urlString = "$BASE_URL/swaps"
            ) {
                contentType(ContentType.Application.Json)
                setBody(swap)
            }
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

    override suspend fun deleteSwap(itemId: String): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.delete(
                urlString = "$BASE_URL/swaps/${itemId}"
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

    //============================= Notification ==================================

    suspend fun getUserNotifications(userId: Int): Result<List<Notification>, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/notifications/${userId}")
        }
    }

    suspend fun updateNotificationRead(notificationId: Int): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.patch(
                urlString = "$BASE_URL/notifications/$notificationId/read"
            )
        }
    }

    suspend fun dismissNotification(notificationId: Int): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.delete(
                urlString = "$BASE_URL/notifications/$notificationId"
            )
        }
    }

    suspend fun clearNotification(userId: Int): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.delete(
                urlString = "$BASE_URL/notifications/$userId/clear"
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

    //Items

    suspend fun getAllItems(): Result<List<ItemDto>, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/item")
        }
    }

    suspend fun createItem(item: ItemDto): Result<ItemDto, DataError.Remote> {
        return safeCall {
            httpClient.post("$BASE_URL/item") {
                contentType(ContentType.Application.Json)
                setBody(item)
            }
        }
    }

    suspend fun getItemById(itemId: String): Result<ItemDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/item/$itemId")
        }
    }

    suspend fun createItemWithFileUpload(item: ItemDto, file: ByteArray?): Result<ItemDto, DataError.Remote> {
        return safeCall {
            httpClient.post("$BASE_URL/item/upload") {
                contentType(ContentType.MultiPart.FormData)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("id", item.id)
                            append("wardrobeId", item.wardrobeId)
                            append("itemType", item.itemType)
                            append("tags", item.tags.joinToString(","))
                            append("createdAt", item.createdAt)

                            // Optional file upload
                            file?.let {
                                append("file", it, Headers.build {
                                    append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"file.jpg\"")
                                })
                            }
                        }
                    )
                )
            }
        }
    }


    suspend fun uploadFile(file: ByteArray): Result<String, DataError.Remote> {
        return safeCall {
            println("Uploading File...")

            val response = httpClient.post("$BASE_URL/blob/upload") {
                contentType(ContentType.MultiPart.FormData)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                key = "file", // Ensure this matches the expected field name
                                value = file,
                                headers = Headers.build {
                                    append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"file.png\"")
                                    append(HttpHeaders.ContentType, "image/png")
                                }
                            )
                        }
                    )
                )
            }

            println("Upload Response: ${response.status}")
            println("Response Body: ${response.bodyAsText()}")

            response.body()
        }
    }





}
