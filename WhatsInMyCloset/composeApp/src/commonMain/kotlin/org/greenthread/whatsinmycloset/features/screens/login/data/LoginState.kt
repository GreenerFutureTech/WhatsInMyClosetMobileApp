package org.greenthread.whatsinmycloset.features.screens.login.data

data class LoginState(
//    val email: String = "",
//    val password: String = "",
    val currentUserId: String = "",
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
