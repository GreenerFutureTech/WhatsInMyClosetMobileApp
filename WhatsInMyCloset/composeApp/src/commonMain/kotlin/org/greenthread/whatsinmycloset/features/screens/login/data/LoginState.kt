package org.greenthread.whatsinmycloset.features.screens.login.data

data class LoginState(
//    val email: String = "",
//    val password: String = "",
    var currentUserId: String = "",
    var isAuthenticated: Boolean = false,
    var errorMessage: String? = null,
    var isLoading: Boolean = false
)
