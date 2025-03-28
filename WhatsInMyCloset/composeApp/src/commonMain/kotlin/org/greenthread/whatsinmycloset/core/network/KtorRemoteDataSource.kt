package org.greenthread.whatsinmycloset.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import org.greenthread.whatsinmycloset.core.data.safeCall
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.CalendarDto
import org.greenthread.whatsinmycloset.core.dto.CreateSwapRequestDto
import org.greenthread.whatsinmycloset.core.dto.FriendRequestDto
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.OtherSwapDto
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.dto.OutfitResponse
import org.greenthread.whatsinmycloset.core.dto.SendMessageRequest
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.SwapStatusDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationDto
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationType
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.SendNotificationRequest
import org.greenthread.whatsinmycloset.features.tabs.profile.domain.RequestStatus
import org.greenthread.whatsinmycloset.features.tabs.profile.data.FriendshipStatus
import org.greenthread.whatsinmycloset.getPlatform

private val platform = getPlatform()
//private val BASE_URL = if (platform.name == "iOS") "http://127.0.0.1:13000" else "http://10.0.2.2:13000"
private const val BASE_URL = "https://green-api-c9h6f7huhuezbuhv.eastus2-01.azurewebsites.net"


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

    override suspend fun getFriendsSwaps(currentUserId: String): Result<List<OtherSwapDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/swaps/friends/$currentUserId"
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

            println("SEND MESSAGE REQUEST $request")

            httpClient.post(
                urlString = "$BASE_URL/messages"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    override suspend fun getUnread(userId: Int): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/messages/$userId/unread")
        }
    }

    //============================= Notification ==================================

    override suspend fun getUserNotifications(userId: Int): Result<List<Notification>, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/notifications/${userId}")
        }
    }

    override suspend fun updateNotificationRead(notificationId: Int): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.patch(
                urlString = "$BASE_URL/notifications/$notificationId/read"
            )
        }
    }

    override suspend fun dismissNotification(notificationId: Int): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.delete(
                urlString = "$BASE_URL/notifications/$notificationId"
            )
        }
    }

    override suspend fun clearNotification(userId: Int): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.delete(
                urlString = "$BASE_URL/notifications/$userId/clear"
            )
        }
    }

    override suspend fun sendNotification(userId: Int, title: String, body: String, type : NotificationType, extraData: Map<String, String>?): Result<NotificationDto, DataError.Remote> {
        return safeCall {
            val request = SendNotificationRequest(
                userId = userId,
                title = title,
                body = body,
                type = type,
                extraData = extraData
            )
            println("SEND NOTIFICATION REQUEST $request")

            httpClient.post(
                urlString = "$BASE_URL/notifications"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
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

    override suspend fun searchUserByUsername(username: String): Result<List<UserDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/users/search/username?query=$username"
            )
        }
    }

    //Wardrobes
    suspend fun getAllWardrobesForUser(userId: String): Result<List<WardrobeEntity>, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/wardrobes/user/$userId")
        }
    }

    suspend fun AddWardrobe(wardrobe: WardrobeEntity): Result<List<WardrobeEntity>, DataError.Remote> {
        return safeCall {
            httpClient.post(
                "$BASE_URL/wardrobes"
            ) {
                contentType(ContentType.Application.Json)
                setBody(wardrobe)
            }
        }
    }

    //Items

    suspend fun getAllItemsForUser(userId: String): Result<List<ItemDto>, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/item/user/$userId")
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

    override suspend fun getItemById(itemId: String): Result<ItemDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/item/$itemId")
        }
    }

    suspend fun createItemWithFileUpload(
        item: ItemDto,
        file: ByteArray?
    ): Result<ItemDto, DataError.Remote> {
        return safeCall {
            httpClient.post("$BASE_URL/item/upload") {
                contentType(ContentType.MultiPart.FormData)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("name", item.name)
                            append("wardrobeId", item.wardrobeId)
                            append("itemType", item.itemType)
                            append("tags", item.tags.joinToString(","))
                            append("brand", item.brand)
                            append("condition", item.condition)
                            append("size", item.size)
                            append("createdAt", item.createdAt)

                            // Optional file upload
                            file?.let {

                                append("file", file, Headers.build {
                                    append(HttpHeaders.ContentType, "image/bmp")
                                    append(HttpHeaders.ContentDisposition, "name=\"file.bmp\"; filename=test.bmp")
                                })
                            }
                        }
                    )
                )
            }
        }
    }


    suspend fun uploadFile(
        byteArray: Any?,
        fileName: String = "image.bmp"
    ): Result<String, DataError.Remote> {
        val data = byteArray as ByteArray
            return safeCall {
                val response: HttpResponse = httpClient.post("$BASE_URL/blob/upload") {
                    contentType(ContentType.MultiPart.FormData)
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                appendInput(
                                    key = "file",
                                    headers = Headers.build {
                                        append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$fileName\"")
                                        append(HttpHeaders.ContentType, "image/bmp") // Ensure correct MIME type
                                    }) {
                                    buildPacket { writeFully(data) }
                                }
                            }
                        )
                    )
                }
                response
            }
    }

    suspend fun uploadImage(filename: String, imageBytes: ByteArray): Result<String, DataError.Remote> {
        return safeCall {
            httpClient.post("$BASE_URL/blob/upload") {

                setBody(MultiPartFormDataContent(
                    formData {
                        append("file", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/bmp")
                            append(HttpHeaders.ContentDisposition, "name=\"file.bmp\"; filename=test.bmp")
                        })
                    }
                ))
            }
        }
    }

    //============================= Outfit ==================================
    override suspend fun getAllOutfits(): Result<List<OutfitDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/outfits"
            )
        }
    }

    override suspend fun getFriendsOutfits(userId: Int): Result<List<OutfitDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/outfits/friends/$userId"
            )
        }
    }

    // outfits -- get outfits
    override suspend fun getAllOutfitsForUser(userId: String): Result<List<OutfitDto>, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/outfits/user/$userId")
        }
    }

    // outfits -- post outfits
    override suspend fun postOutfitForUser(outfit: OutfitDto): Result<OutfitResponse, DataError.Remote> {
        return safeCall {
            httpClient.post("$BASE_URL/outfits") {
                    contentType(ContentType.Application.Json)
                    setBody(outfit)
                }
        }
    }

    // calendar -- post outfit to calendar
    override suspend fun postOutfitToCalendar(calendarDto: CalendarDto): Result<List<CalendarDto>, DataError.Remote> {
        return safeCall {
            httpClient.post("$BASE_URL/calendar") {
                contentType(ContentType.Application.Json)
                setBody(calendarDto)
            }
        }
    }


    // calendar -- get outfit from calendar
    override suspend fun getAllOutfitsFromCalendar(userId: String): Result<List<CalendarDto>, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/calendar/user/$userId")
        }
    }

    override suspend fun getUserByUserName(username: String): Result<UserDto, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/users/username/$username"
            )
        }
    }

    override suspend fun sendFriendRequest(senderId: Int, receiverId: Int): Result<Unit, DataError.Remote> {
        return safeCall {
            httpClient.post(
                urlString = "$BASE_URL/users/$senderId/friend-request/$receiverId"
            )
        }
    }

    override suspend fun getSentFriendRequests(userId: Int): Result<List<FriendRequestDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/users/$userId/friend-requests/sent"
            )
        }
    }

    override suspend fun getReceivedFriendRequests(userId: Int): Result<List<FriendRequestDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/users/$userId/friend-requests"
            )
        }
    }

    override suspend fun respondToFriendRequest(requestId: Int, status: RequestStatus): Result<Unit, DataError.Remote> {
        return safeCall {
            httpClient.post(
                urlString = "$BASE_URL/users/$requestId/respond/${status.name}"
            )
        }
    }

    override suspend fun removeFriend(userId: Int, friendId: Int): Result<Unit, DataError.Remote> {
        return safeCall {
            httpClient.delete(
                urlString = "$BASE_URL/users/$userId/friends/$friendId"
            )
        }
    }

    override suspend fun cancelFriendRequest(senderId: Int, receiverId: Int): Result<Unit, DataError.Remote> {
        return safeCall {
            httpClient.delete(
                urlString = "$BASE_URL/users/friend-request/$senderId/$receiverId"
            )
        }
    }
}


