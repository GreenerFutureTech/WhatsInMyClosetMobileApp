package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val name: String,
    val profilePicture: String,
    val registeredAt: String,
    val updatedAt: String,
    val lastLogin: String
)