package org.greenthread.whatsinmycloset.core.network

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.CalendarDto
import org.greenthread.whatsinmycloset.core.dto.CalendarResponse
import org.greenthread.whatsinmycloset.core.dto.CreateSwapRequestDto
import org.greenthread.whatsinmycloset.core.dto.FriendRequestDto
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.OtherSwapDto
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.dto.OutfitResponse
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.SwapStatusDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationDto
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationType
import org.greenthread.whatsinmycloset.features.tabs.profile.domain.RequestStatus

interface RemoteClosetDataSource {
    // Swap
    suspend fun getSwaps(userId: String): Result<List<SwapDto>, DataError.Remote>
    suspend fun getFriendsSwaps(currentUserId: String): Result<List<OtherSwapDto>, DataError.Remote>
    suspend fun getAllSwaps(): Result<List<SwapDto>, DataError.Remote>
    suspend fun createSwap(swap: CreateSwapRequestDto): Result<CreateSwapRequestDto, DataError.Remote>
    suspend fun updateStatus(itemId: String): Result<SwapStatusDto, DataError.Remote>
    suspend fun deleteSwap(itemId: String): Result<String, DataError.Remote>

    // Messages
    suspend fun getLatestMessage(userId: String): Result<List<MessageDto>, DataError.Remote>
    suspend fun getChatHistory(userId: Int, otherUserId: Int): Result<List<MessageDto>, DataError.Remote>
    suspend fun sendMessage(senderId: Int, receiverId: Int, content: String): Result<MessageDto, DataError.Remote>
    suspend fun updateRead(messageId: Int): Result<String, DataError.Remote>
    suspend fun getUnread(userId: Int): Result<String, DataError.Remote>

    // User
    suspend fun createUser(user: UserDto): Result<UserDto, DataError.Remote>
    suspend fun getUser(userEmail: String): Result<UserDto, DataError.Remote>
    suspend fun getUserById(userId: Int): Result<UserDto, DataError.Remote>
    suspend fun updateUser(user: UserDto): Result<UserDto, DataError.Remote>
    suspend fun searchUserByUsername(username: String): Result<List<UserDto>, DataError.Remote>
    suspend fun getUserByUserName(username: String): Result<UserDto, DataError.Remote>

    // Outfit
    suspend fun getAllOutfits(): Result<List<OutfitDto>, DataError.Remote>
    suspend fun getAllOutfitsForUser(userId: Int): Result<List<OutfitDto>, DataError.Remote>
    suspend fun postOutfitForUser(outfit: OutfitDto): Result<OutfitResponse, DataError.Remote>
    suspend fun getFriendsOutfits(userId: Int): Result<List<OutfitDto>, DataError.Remote>
    suspend fun getOutfitById(outfitId: String): Result<OutfitDto, DataError.Remote>

    // Calendar
    suspend fun postOutfitToCalendar(calendarDto: CalendarDto): Result<CalendarResponse, DataError.Remote>
    suspend fun getAllOutfitsFromCalendar(userId: String): Result<List<CalendarDto>, DataError.Remote>

    // Notifications
    suspend fun sendNotification(userId: Int, title: String, body: String, type : NotificationType, extraData: Map<String, String>? = null): Result<NotificationDto, DataError.Remote>
    suspend fun clearNotification(userId: Int): Result<String, DataError.Remote>
    suspend fun updateNotificationRead(notificationId: Int): Result<String, DataError.Remote>
    suspend fun dismissNotification(notificationId: Int): Result<String, DataError.Remote>
    suspend fun getUserNotifications(userId: Int): Result<List<Notification>, DataError.Remote>

    // Item
    suspend fun getItemById(itemId: String): Result<ItemDto, DataError.Remote>

    // Friend request
    suspend fun sendFriendRequest(senderId: Int, receiverId: Int): Result<Unit, DataError.Remote>
    suspend fun getSentFriendRequests(userId: Int, forceRefresh: Boolean = false): Result<List<FriendRequestDto>, DataError.Remote>
    suspend fun getReceivedFriendRequests(userId: Int, forceRefresh: Boolean = false): Result<List<FriendRequestDto>, DataError.Remote>
    suspend fun respondToFriendRequest(requestId: Int, status: RequestStatus): Result<Unit, DataError.Remote>
    suspend fun removeFriend(userId: Int, friendId: Int): Result<Unit, DataError.Remote>
    suspend fun cancelFriendRequest(senderId: Int, receiverId: Int): Result<Unit, DataError.Remote>
    suspend fun getFriendsByUserId(userId: Int, forceRefresh: Boolean = false): Result<List<UserDto>, DataError.Remote>
}
