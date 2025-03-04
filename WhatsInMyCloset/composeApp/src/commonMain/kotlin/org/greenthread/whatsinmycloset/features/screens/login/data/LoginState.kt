package org.greenthread.whatsinmycloset.features.screens.login.data

data class LoginState(
    var isAuthenticated: Boolean = false,
    var errorMessage: String? = null,
    var isLoading: Boolean = false
)
