package org.greenthread.whatsinmycloset.features.screens.login.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.features.screens.login.data.LoginState
import org.greenthread.whatsinmycloset.features.screens.login.domain.LoginAction
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.features.screens.notifications.presentation.NotificationsViewModel
import org.greenthread.whatsinmycloset.getFCMToken


class LoginViewModel(
    private val userRepository: ClosetRepository,
    private val userManager: UserManager,
    private val notificationsViewModel: NotificationsViewModel
): ViewModel() {
    private val auth = Firebase.auth

    // View Model State variables to track login details
    private val _state = mutableStateOf(LoginState(isLoading = true))
    val state by _state

    // Lambda Functions that can be called from the Login Screen
    var onLoginSuccess: (() -> Unit)? = null
    var onSignupSuccess: (() -> Unit)? = null

    init {
        checkCurrentUser()
    }

    /**
     * Logs out the current user from Firebase and clears the user data.
     * This function also clears the new notifications state.
     */
    fun logout() {
        viewModelScope.launch {
            auth.signOut() // Sign out from Firebase
            userManager.updateUser(null)
            notificationsViewModel.clearNewNotificationsState()
        }
    }

    /**
     * Checks if there is a currently authenticated user.
     * If a user is found, it updates the state to reflect authentication and fetches user details.
     * If no user is found, it sets the loading state to false.
     */
    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _state.value = state.copy(isAuthenticated = true, isLoading = true)
            getUser(currentUser.email ?: "")
        } else {
            _state.value = state.copy(isLoading = false)
        }
    }

    /**
     * Handles different login actions such as sign-in and sign-up.
     *
     * @param action The login action to perform, which can be either `SignIn` or `SignUp`.
     */
    fun onAction(action: LoginAction){
        when(action) {
            is LoginAction.SignIn -> signIn(action.email, action.password)
            is LoginAction.SignUp -> signUp(action.email, action.password, action.username, action.name)
        }
    }

    /**
     * Signs in a user with the provided email and password.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    private fun signIn(email: String, password:String) {
        _state.value = state.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password)

                if (result.user != null)
                {
                    getUser(email)
                }
                else
                {
                    _state.value = state.copy(
                        errorMessage = "Authentication failed",
                        isLoading = false,
                        isAuthenticated = false
                    )
                }

            } catch (e: Exception) {
                _state.value = state.copy(
                    errorMessage = e.message?: "Failed Login",
                    isLoading = false,
                    isAuthenticated = false
                )
            }
        }
    }

    /**
     * Signs up a new user with the provided email, password, username, and name.
     *
     * @param email The email of the new user.
     * @param password The password of the new user.
     * @param username The username of the new user.
     * @param name The name of the new user.
     */
    private fun signUp(email: String, password: String, username:String, name:String) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        _state.value = state.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password)

                val token = getFCMToken()

                val userDto = UserDto(
                    username = username,
                    email = email,
                    name = name,
                    firebaseUid = result.user?.uid ?: "",
                    fcmToken = token,
                    type = "User",
                    registeredAt = now.toString(),
                    updatedAt = now.toString(),
                    lastLogin = now.toString()
                )

                createUser(userDto)

                _state.value = state.copy(
                    isLoading = false
                )
                onSignupSuccess?.invoke()
            } catch (e: Exception) {
                _state.value = state.copy(
                    errorMessage = e.message?: "Failed Sign Up",
                    isLoading = false
                )
            }
        }
    }

    /**
     * Creates a new user in the repository.
     *
     * @param user The user data to be created, represented as a `UserDto`.
     */
    private fun createUser(user: UserDto) {
        viewModelScope.launch {
            println("CREATE USER : Create user")
            _state.value = state.copy(
                isLoading = true
            )
            userRepository
                .createUser(user)
                .onSuccess { getResults ->
                    println("CREATE USER  API success: $getResults")
                    _state.value = state.copy(
                        isLoading = false,
                    )
                }
                .onError { error ->
                    println("CREATE USER  API ERROR $error")
                    _state.value = state.copy(
                        isLoading = false,
                    )
                }
        }
    }

    /**
     * Fetches user details from the repository using the provided email.
     *
     * @param email The email of the user to fetch.
     */
    private fun getUser(email: String) {
        viewModelScope.launch {
            println("GET USER : Get user")
            _state.value = state.copy(
                isLoading = true
            )
            userRepository
                .getUser(email)
                .onSuccess { userDto ->
                    println("GET USER ${userDto.id} SUCCESS")

                    userManager.updateUser(userDto.toModel())

                    userManager.currentUser.value?.retrieveUserId()?.let { userId ->
                        notificationsViewModel.checkForUnreadNotifications(userId)
                    }

                    val token = getFCMToken()
                    val updatedUser = userDto.copy(
                        fcmToken = token,
                        lastLogin = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
                    )
                    updateUser(updatedUser)

                    _state.value = state.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )

                    onLoginSuccess?.invoke()
                }
                .onError { error ->
                    println("GET USER API ERROR $error")
                    _state.value = state.copy(
                        isLoading = false,
                    )
                }
        }
    }

    /**
     * Updates the user details in the repository.
     *
     * @param user The user data to be updated, represented as a `UserDto`.
     */
    private fun updateUser(user: UserDto) {
        viewModelScope.launch {
            println("UPDATE USER : Updated user ${user.id}")
            _state.value = state.copy(
                isLoading = true
            )
            userRepository
                .updateUser(user)
                .onSuccess { getResults ->
                    println("UPDATE USER  API SUCCESS: $getResults")
                    _state.value = state.copy(
                        isLoading = false,
                    )
                }
                .onError { error ->
                    println("UPDATE USER  API ERROR $error")
                    _state.value = state.copy(
                        isLoading = false,
                    )
                }
        }
    }
}