package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.User

@Serializable
data class UserDto(
    val id: Int? = null,
    val username: String,
    val email: String,
    val name: String,
    val firebaseUid: String,
    val profilePicture: String? = null,
    val type: String? = null,
    val registeredAt: String,
    val updatedAt: String,
    val lastLogin: String
) {
    fun toModel(): User {
        return User(id = id, name = name, username = username,
            email = email, firebaseUuid = firebaseUid,
            profilePicture = profilePicture, type = type,
            registeredAt = registeredAt, updatedAt = updatedAt, lastLogin = lastLogin)
    }
}
