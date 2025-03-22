package org.greenthread.whatsinmycloset.core.network

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.SwapStatusDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationDto
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationType

interface RemoteClosetDataSource {
    // Swap
    suspend fun getSwaps(userId: String): Result<List<SwapDto>, DataError.Remote>
    suspend fun getOtherUsersSwaps(currentUserId: String): Result<List<SwapDto>, DataError.Remote>
    suspend fun getAllSwaps(): Result<List<SwapDto>, DataError.Remote>
    suspend fun updateStatus(itemId: String): Result<SwapStatusDto, DataError.Remote>
    suspend fun deleteSwap(itemId: String): Result<String, DataError.Remote>

    // Messages
    suspend fun getLatestMessage(userId: String): Result<List<MessageDto>, DataError.Remote>
    suspend fun getChatHistory(userId: Int, otherUserId: Int): Result<List<MessageDto>, DataError.Remote>
    suspend fun sendMessage(senderId: Int, receiverId: Int, content: String): Result<MessageDto, DataError.Remote>
    suspend fun updateRead(messageId: Int): Result<String, DataError.Remote>

    // User
    suspend fun createUser(user: UserDto): Result<UserDto, DataError.Remote>
    suspend fun getUser(userEmail: String): Result<UserDto, DataError.Remote>
    suspend fun getUserById(userId: Int): Result<UserDto, DataError.Remote>
    suspend fun updateUser(user: UserDto): Result<UserDto, DataError.Remote>

    // Notifications
    suspend fun sendNotification(userId: Int, title: String, body: String, type : NotificationType, extraData: Map<String, String>? = null): Result<NotificationDto, DataError.Remote>
    suspend fun clearNotification(userId: Int): Result<String, DataError.Remote>
    suspend fun updateNotificationRead(notificationId: Int): Result<String, DataError.Remote>
    suspend fun dismissNotification(notificationId: Int): Result<String, DataError.Remote>
    suspend fun getUserNotifications(userId: Int): Result<List<Notification>, DataError.Remote>


}
