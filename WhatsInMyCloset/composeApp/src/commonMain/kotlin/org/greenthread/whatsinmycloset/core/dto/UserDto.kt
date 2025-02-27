package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int? = null,
    val username: String,
    val email: String,
    val name: String,
    val firebaseUid: String,
    val profilePicture: String? = null,
    val registeredAt: String,
    val updatedAt: String,
    val lastLogin: String
)