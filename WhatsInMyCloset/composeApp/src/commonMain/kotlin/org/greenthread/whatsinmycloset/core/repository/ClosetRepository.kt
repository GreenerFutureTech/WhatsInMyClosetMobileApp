package org.greenthread.whatsinmycloset.core.repository

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto

interface ClosetRepository {
    // Swap
    suspend fun getSwaps(userId: String) : Result<List<SwapDto>,DataError.Remote>
    suspend fun getOtherUsersSwaps(currentUserId: String): Result<List<SwapDto>, DataError.Remote>
    suspend fun getAllSwaps(): Result<List<SwapDto>, DataError.Remote>

    // Messages
    suspend fun getLatestMessage(userId: String): Result<List<MessageDto>, DataError.Remote>
    suspend fun getChatHistory(userId: Int, otherUserId: String): Result<List<MessageDto>, DataError.Remote>
    // User
    suspend fun createUser(user: UserDto): Result<UserDto, DataError.Remote>
    suspend fun getUser(userEmail: String) : Result<UserDto,DataError.Remote>
    suspend fun getUserById(userId: Int): Result<UserDto, DataError.Remote>
    suspend fun updateUser(user: UserDto): Result<UserDto, DataError.Remote>
}