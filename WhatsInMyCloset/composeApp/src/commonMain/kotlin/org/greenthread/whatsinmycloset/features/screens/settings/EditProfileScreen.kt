package org.greenthread.whatsinmycloset.features.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.theme.outlineVariantLight
import org.greenthread.whatsinmycloset.theme.secondaryLight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.email_text
import whatsinmycloset.composeapp.generated.resources.name_text
import whatsinmycloset.composeapp.generated.resources.username_text

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val currentUser = viewModel.currentUser
    var isEditMode by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(currentUser.value?.name ?: "") }
    var username by remember { mutableStateOf(currentUser.value?.username ?: "") }
    var email by remember { mutableStateOf(currentUser.value?.email ?: "") }

    if (currentUser == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        var profileLoadFailed by remember { mutableStateOf(false) }
        @OptIn(ExperimentalResourceApi::class)
        AsyncImage(
            model = if (profileLoadFailed) Res.getUri("drawable/defaultUser.png") else currentUser.value?.profilePicture,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(1.dp, secondaryLight, CircleShape)
                .align(Alignment.CenterHorizontally),
        onError = { profileLoadFailed = true },
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(
                text = stringResource(Res.string.email_text),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = email,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (!isEditMode) {

            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = stringResource(Res.string.name_text),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = stringResource(Res.string.username_text),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = username,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        } else {
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                singleLine = true
            )
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                singleLine = true
            )
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        Button(
            onClick = {
                if (isEditMode) {
                    //viewModel.updateUser() // TODO: API CALL
                }
                isEditMode = !isEditMode
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (isEditMode) "Save" else "Edit")
        }
    }
}
