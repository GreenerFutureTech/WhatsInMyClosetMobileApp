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


class LoginViewModel(
    private val userRepository: ClosetRepository
): ViewModel() {
    private val auth = Firebase.auth
    private val _state = mutableStateOf(LoginState())
    val state by _state
    var onLoginSuccess: (() -> Unit)? = null
    var onSignupSuccess: (() -> Unit)? = null

    fun onAction(action: LoginAction){
        when(action) {
            is LoginAction.SignIn -> signIn(action.email, action.password)
            is LoginAction.SignUp -> signUp(action.email, action.password, action.username, action.name)
        }
    }

    private fun signIn(email: String, password:String) {
        _state.value = state.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password)

                getUser(email)

                _state.value = state.copy(
                    isAuthenticated = true,
                    isLoading = false
                )
                onLoginSuccess?.invoke()
            } catch (e: Exception) {
                _state.value = state.copy(
                    errorMessage = e.message?: "Failed Login",
                    isLoading = false
                )
            }
        }
    }

    private fun signUp(email: String, password: String, username:String, name:String) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        _state.value = state.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password)

                val userDto = UserDto(
                    username = username,
                    email = email,
                    name = name,
                    firebaseUid = result.user?.uid?:"",
                    registeredAt = now.toString(),
                    updatedAt = now.toString(),
                    lastLogin = now.toString()
                )

                createUser(userDto)

                _state.value = state.copy(
                    isAuthenticated = true,
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

    fun createUser(user: UserDto) {
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
                    println("CREATE USER  API ERROR ${error}")
                    _state.value = state.copy(
                        isLoading = false,
                    )
                }
        }
    }

    fun getUser(email: String) {
        viewModelScope.launch {
            println("GET USER : Get user")
            _state.value = state.copy(
                isLoading = true
            )
            userRepository
                .getUser(email)
                .onSuccess { user ->
                    println("GET USER ${user.id} SUCCESS")

                    UserManager.currentUser = user

                    val updatedUser = user.copy(
                        lastLogin = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
                    )
                    updateUser(updatedUser)

                    _state.value = state.copy(
                        isLoading = false,
                    )
                }
                .onError { error ->
                    println("GET USER API ERROR ${error}")
                    _state.value = state.copy(
                        isLoading = false,
                    )
                }
        }
    }

    fun updateUser(user: UserDto) {
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
                    println("UPDATE USER  API ERROR ${error}")
                    _state.value = state.copy(
                        isLoading = false,
                    )
                }
        }
    }
}