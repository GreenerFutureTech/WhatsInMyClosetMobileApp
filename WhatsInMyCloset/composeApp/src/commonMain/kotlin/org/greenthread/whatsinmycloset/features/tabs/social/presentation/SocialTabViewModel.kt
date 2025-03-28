package org.greenthread.whatsinmycloset.features.tabs.social.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.features.tabs.social.domain.Post

class SocialTabViewModel(
    private val itemRepository: ClosetRepository,
    private val userManager: UserManager
): ViewModel() {
    // State: posts' list
    private val _posts = mutableStateListOf<Post>()
    val posts: List<Post> get() = _posts
    // State: detailed image on click
    private val _selectedImage = mutableStateOf<Int?>(null)
    val selectedImage: Int? get() = _selectedImage.value
    // State: navigation to a friend's profile
    private val _navigateToFriendProfile = MutableStateFlow<String?>(null)
    val navigateToFriendProfile: StateFlow<String?> get() = _navigateToFriendProfile
    fun loadPosts() {
        // TODO fetch data from the backend
        _posts.addAll(
            listOf(
                Post(
                    id = 1,
                    outfit = "https://example.com/image1.jpg",
                    username = "user1",
                    date = "2023-10-01"
                ),
                Post(
                    id = 2,
                    outfit = "https://example.com/image2.jpg",
                    username = "user2",
                    date = "2023-10-02"
                )
            )
        )
    }
    // Function to handle outfit click
    fun onImageClick(outfitId: Int) {
        _selectedImage.value = outfitId
    }
    // Function to handle username click
    fun onUsernameClick(username: String) {
        _navigateToFriendProfile.value = username
    }
    // Function to reset navigation state
    fun onNavigatedToFriendProfile() {
        _navigateToFriendProfile.value = null
    }
    // Function to reset selected post
    fun onImageClosed() {
        _selectedImage.value = null
    }
}