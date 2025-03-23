package org.greenthread.whatsinmycloset.core.repository

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.OtherSwapDto
import org.greenthread.whatsinmycloset.core.dto.SendMessageRequest
import org.greenthread.whatsinmycloset.core.network.RemoteClosetDataSource
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.SwapStatusDto
import org.greenthread.whatsinmycloset.core.dto.UserDto

class DefaultClosetRepository(
    private val remoteClosetDataSource: RemoteClosetDataSource
): ClosetRepository {
    //============================= Swap ==================================
    override suspend fun getSwaps(userId: String): Result<List<SwapDto>, DataError.Remote> {
        return remoteClosetDataSource.getSwaps(userId)
    }

    override suspend fun getFriendsSwaps(currentUserId: String): Result<List<OtherSwapDto>, DataError.Remote> {
        return remoteClosetDataSource.getFriendsSwaps(currentUserId)
    }

    override suspend fun getAllSwaps(): Result<List<SwapDto>, DataError.Remote> {
        return remoteClosetDataSource.getAllSwaps()
    }

    override suspend fun updateStatus(itemId: String): Result<SwapStatusDto, DataError.Remote> {
        return remoteClosetDataSource.updateStatus(itemId)
    }

    override suspend fun deleteSwap(itemId: String): Result<String, DataError.Remote> {
        return remoteClosetDataSource.deleteSwap((itemId))
    }

    //============================= Messages ==================================
    override suspend fun getLatestMessage(userId: String): Result<List<MessageDto>, DataError.Remote> {
        return remoteClosetDataSource.getLatestMessage(userId)
    }

    override suspend fun getChatHistory(userId: Int, otherUserId: Int): Result<List<MessageDto>, DataError.Remote> {
        return remoteClosetDataSource.getChatHistory(userId, otherUserId)
    }

    override suspend fun sendMessage(senderId: Int, receiverId: Int, content: String): Result<MessageDto, DataError.Remote> {
        return remoteClosetDataSource.sendMessage(senderId, receiverId, content)
    }

    override suspend fun updateRead(messageId: Int): Result<String, DataError.Remote> {
        return remoteClosetDataSource.updateRead(messageId)
    }

        //============================= User ==================================
    override suspend fun createUser(user: UserDto): Result<UserDto, DataError.Remote> {
        return remoteClosetDataSource.createUser(user)
    }

    override suspend fun getUser(userEmail: String): Result<UserDto, DataError.Remote> {
       return remoteClosetDataSource.getUser(userEmail)
    }

    override suspend fun getUserById(userId: Int): Result<UserDto, DataError.Remote> {
        return remoteClosetDataSource.getUserById(userId)
    }

    override suspend fun updateUser(user: UserDto): Result<UserDto, DataError.Remote> {
        return remoteClosetDataSource.updateUser(user)
    }
}

