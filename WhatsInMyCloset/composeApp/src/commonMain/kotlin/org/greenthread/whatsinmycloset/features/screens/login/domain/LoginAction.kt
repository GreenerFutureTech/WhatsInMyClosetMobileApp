package org.greenthread.whatsinmycloset.features.screens.login.domain

sealed class LoginAction {
    data class SignIn(val email: String, val password: String): LoginAction()
    data class SignUp(val email: String, val password: String, val username: String, val name: String): LoginAction()
}