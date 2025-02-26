package org.greenthread.whatsinmycloset.features.screens.login.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.ui.components.controls.CustomTextField
import org.greenthread.whatsinmycloset.features.screens.login.data.LoginState
import org.greenthread.whatsinmycloset.features.screens.login.domain.LoginAction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.logo

@Composable
@Preview
fun LoginScreenRoot(
    viewModel: LoginViewModel,
    navController: NavController
) {
    viewModel.onLoginSuccess = {
        navController.navigate(Routes.HomeTab)
    }

    LoginScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        navController = navController
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val color = Color(0xFFF2E1D0)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Image(
                painter = painterResource(Res.drawable.logo) ,
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                }
            )

            val errorMessage = state.errorMessage
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAction(LoginAction.SignIn(email, password))},
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = color
                )
            ) {
                Text("Sign In")
            }

            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { navController.navigate(Routes.SignUpTab) }
            ) {
                Text(
                    text = "Don't have an account? Sign Up",
                    fontSize = 12.sp
                )
            }
        }
    }
}
