package org.greenthread.whatsinmycloset.features.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginViewModel

@Composable
fun SettingsScreen(navController: NavController, loginViewModel: LoginViewModel) {
    var isToggleOn by remember { mutableStateOf(false) }
    var showDialogLogout by remember { mutableStateOf(false) }
    var showDialogDeleteAccount by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Title Section
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Navigation Buttons
        SettingsItem(
            title = "Edit Profile",
            onClick = { },
            leftIcon = Icons.Default.AccountCircle,
            rightIcon = Icons.Default.PlayArrow
        )
        SettingsItem(
            title = "Change Password",
            onClick = { },
            leftIcon = Icons.Default.Edit,
            rightIcon = Icons.Default.PlayArrow
        )
        SettingsItem(
            title = "Help and Support",
            onClick = { },
            leftIcon = Icons.Default.Info,
            rightIcon = Icons.Default.PlayArrow
        )

        // Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Notifications",
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = isToggleOn, onCheckedChange = { isToggleOn = it })
        }

        Divider()

        // Actions with Confirmation Dialog
        SettingsItem(
            title = "Log out", onClick = { showDialogLogout = true },
            leftIcon = Icons.Default.Clear
        )
        SettingsItem(
            title = "Delete Account",
            onClick = { showDialogDeleteAccount = true },
            leftIcon = Icons.Default.Delete
        )

        // Confirmation Dialogs
        if (showDialogLogout) {
            ConfirmationDialog(
                title = "Log out",
                message = "Are you sure you want to log out?",
                onConfirm = {
                    showDialogLogout = false
                    loginViewModel.logout()
                    navController.navigate(Routes.LoginTab) {
                        popUpTo(Routes.HomeTab) { inclusive = true }
                    }                },
                onDismiss = { showDialogLogout = false }
            )
        }

        if (showDialogDeleteAccount) {
            ConfirmationDialog(
                title = "Delete Account",
                message = "Are you sure you want to delete your account?",
                onConfirm = {
                    showDialogDeleteAccount = false
                    loginViewModel.logout()
                    navController.navigate(Routes.LoginTab) {
                        popUpTo(Routes.HomeTab) { inclusive = true }
                    }
                },
                onDismiss = { showDialogDeleteAccount = false }
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    leftIcon: ImageVector,
    rightIcon: ImageVector? = null,
    onClick: () -> Unit
) {
    Column {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            border = null // Remove border to blend with background
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    leftIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                if (rightIcon != null) {
                    Icon(
                        rightIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Divider() // Add line below the button
    }
}

@Composable
fun ConfirmationDialog(title: String, message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Confirm") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}