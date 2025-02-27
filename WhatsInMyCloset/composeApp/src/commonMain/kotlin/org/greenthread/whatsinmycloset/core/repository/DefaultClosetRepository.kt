package org.greenthread.whatsinmycloset.core.repository

import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.Result
import org.greenthread.whatsinmycloset.core.network.RemoteClosetDataSource
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto

class DefaultClosetRepository(
    private val remoteClosetDataSource: RemoteClosetDataSource
): ClosetRepository {
    //============================= Swap ==================================
    override suspend fun getSwaps(userId: String): Result<List<SwapDto>, DataError.Remote> {
        return remoteClosetDataSource.getSwaps(userId)
    }

    override suspend fun getOtherUsersSwaps(currentUserId: String): Result<List<SwapDto>, DataError.Remote> {
        return remoteClosetDataSource.getOtherUsersSwaps(currentUserId)
    }

    override suspend fun getAllSwaps(): Result<List<SwapDto>, DataError.Remote> {
        return remoteClosetDataSource.getAllSwaps()
    }

    //============================= User ==================================
    override suspend fun createUser(user: UserDto): Result<UserDto, DataError.Remote> {
        return remoteClosetDataSource.createUser(user)
    }

    override suspend fun getUser(userEmail: String): Result<UserDto, DataError.Remote> {
       return remoteClosetDataSource.getUser(userEmail)
    }

    override suspend fun updateUser(user: UserDto): Result<UserDto, DataError.Remote> {
        return remoteClosetDataSource.updateUser(user)
    }
}

