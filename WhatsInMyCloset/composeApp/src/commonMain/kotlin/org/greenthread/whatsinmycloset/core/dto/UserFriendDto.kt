package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserFriendDto(
    val id: Int? = null,
    val username: String,
    val name: String,
    val profilePicture: String? = null
)
