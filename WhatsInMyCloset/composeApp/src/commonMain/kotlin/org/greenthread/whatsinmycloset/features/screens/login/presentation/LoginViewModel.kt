package org.greenthread.whatsinmycloset.features.screens.login.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.features.screens.login.data.LoginState
import org.greenthread.whatsinmycloset.features.screens.login.domain.LoginAction

class LoginViewModel(): ViewModel() {
    //private val auth = Firebase.auth
    private val _state = mutableStateOf(LoginState())
    val state by _state
    var onLoginSuccess: (() -> Unit)? = null
    var onSignupSuccess: (() -> Unit)? = null

    fun onAction(action: LoginAction){
        when(action) {
            is LoginAction.SignIn -> signIn(action.email, action.password)
            is LoginAction.SignUp -> signUp(action.email, action.password)
        }
    }

    private fun signIn(email: String, password:String) {
        _state.value = state.copy(isLoading = true)

        viewModelScope.launch {
            try {
                //val result = auth.signInWithEmailAndPassword(email, password)
                _state.value = state.copy(
                    isAuthenticated = true,
                    //currentUserId = result.user?.uid?: "",
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

    private fun signUp(email: String, password: String) {
        _state.value = state.copy(isLoading = true)

        viewModelScope.launch {
            try {
                //val result = auth.createUserWithEmailAndPassword(email, password)
                _state.value = state.copy(
                    isAuthenticated = true,
                    //currentUserId = result.user?.uid?: "",
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
}