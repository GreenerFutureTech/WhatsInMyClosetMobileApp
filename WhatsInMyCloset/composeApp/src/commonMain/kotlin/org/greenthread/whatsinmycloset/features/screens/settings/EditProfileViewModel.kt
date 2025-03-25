package org.greenthread.whatsinmycloset.features.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.isSuccess
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource


class EditProfileViewModel(
    private val userManager: UserManager,
    private val httpRepository: KtorRemoteDataSource,
) : ViewModel() {


    val currentUser = userManager.currentUser

    fun updateProfile(image: ByteArray?, name: String, username: String, callback: (Boolean, String?) -> Unit) {
        val user = currentUser.value?.toUserDto()

        viewModelScope.launch {
            try {
                val profilePictureUrl = image?.let {
                    val result = httpRepository.uploadImage("fileName.bmp", it)
                    if (result.isSuccess()) result.getOrNull() else null
                } ?: user?.profilePicture

                val updatedUser = user?.copy(
                    profilePicture = profilePictureUrl ?: user.profilePicture,
                    name = name,
                    username = username
                )

                updatedUser?.let {
                    httpRepository.updateUser(it)
                        .onSuccess { response ->
                            println("UPDATE USER API SUCCESS: $response")
                            callback(true, null)
                        }
                        .onError { error ->
                            println("UPDATE USER API ERROR: $error")
                            callback(false, "Failed to update profile")
                        }
                } ?: callback(false, "User not found")

            } catch (e: Exception) {
                callback(false, e.message)
            }
        }
    }
}

