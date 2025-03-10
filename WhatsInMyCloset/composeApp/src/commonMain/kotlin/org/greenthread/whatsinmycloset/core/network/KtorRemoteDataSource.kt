package org.greenthread.whatsinmycloset.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.greenthread.whatsinmycloset.core.data.safeCall
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.getPlatform

private val platform = getPlatform()
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
            httpClient.post("$BASE_URL/blob/upload") {
                contentType(ContentType.MultiPart.FormData)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("file", file, Headers.build {
                                append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"file.jpg\"")
                            })
                        }
                    )
                )
            }
        }
    }
}
