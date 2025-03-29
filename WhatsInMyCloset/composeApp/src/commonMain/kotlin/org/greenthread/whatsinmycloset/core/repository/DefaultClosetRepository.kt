package org.greenthread.whatsinmycloset.core.repository

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.dto.CreateSwapRequestDto
import org.greenthread.whatsinmycloset.core.dto.FriendRequestDto
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.OtherSwapDto
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.network.RemoteClosetDataSource
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.SwapStatusDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.features.tabs.profile.domain.RequestStatus

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

    override suspend fun createSwap(swap: CreateSwapRequestDto): Result<CreateSwapRequestDto, DataError.Remote> {
        return remoteClosetDataSource.createSwap(swap)
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

    override suspend fun getUnread(userId: Int): Result<String, DataError.Remote> {
        return remoteClosetDataSource.getUnread(userId)
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

    //============================= Item ==================================
    override suspend fun getItemById(itemId: String): Result<ItemDto, DataError.Remote> {
        return remoteClosetDataSource.getItemById(itemId)
    }
    //============================= Outfit ==================================
    override suspend fun getAllOutfits(): Result<List<OutfitDto>, DataError.Remote> {
        return remoteClosetDataSource.getAllOutfits()
    }

    override suspend fun getOutfitById(outfitId: String): Result<OutfitDto, DataError.Remote> {
        return remoteClosetDataSource.getOutfitById(outfitId)
    }

    override suspend fun getUserByUserName(username: String): Result<UserDto, DataError.Remote> {
        return remoteClosetDataSource.getUserByUserName(username)
    }

    override suspend fun sendFriendRequest(senderId: Int, receiverId: Int): Result<Unit, DataError.Remote> {
        return remoteClosetDataSource.sendFriendRequest(senderId, receiverId)
    }

    override suspend fun respondToFriendRequest(requestId: Int, status: RequestStatus): Result<Unit, DataError.Remote> {
        return remoteClosetDataSource.respondToFriendRequest(requestId, status)
    }

    override suspend fun getReceivedFriendRequests(userId: Int): Result<List<FriendRequestDto>, DataError.Remote> {
        return remoteClosetDataSource.getReceivedFriendRequests(userId)
    }

    override suspend fun getSentFriendRequests(userId: Int): Result<List<FriendRequestDto>, DataError.Remote> {
        return remoteClosetDataSource.getSentFriendRequests(userId)
    }

    override suspend fun removeFriend(userId: Int, friendId: Int): Result<Unit, DataError.Remote> {
        return remoteClosetDataSource.removeFriend(userId, friendId)
    }

    override suspend fun cancelFriendRequest(senderId: Int, receiverId: Int): Result<Unit, DataError.Remote> {
        return remoteClosetDataSource.cancelFriendRequest(senderId, receiverId)
    }
}

