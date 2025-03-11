package org.greenthread.whatsinmycloset.features.tabs.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.SeeAllButton
import org.greenthread.whatsinmycloset.getScreenWidthDp
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.available_swap_title
import whatsinmycloset.composeapp.generated.resources.profileUser

@Composable
fun ProfilePicture() {
    WhatsInMyClosetTheme {
        // Set profile picture proportional to the phone screen size
        val screenWidth = getScreenWidthDp()
        var imageSize = screenWidth * 0.2f // Adjust the percentage as needed

        Image(
            painter = painterResource(resource = Res.drawable.profileUser),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colors.primary, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun Username(user: String) {
    Text(
        text = user,
        style = MaterialTheme.typography.h5,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun FriendsCount(friendsCount: Int) {
    Text(
        text = "$friendsCount friends",
        style = MaterialTheme.typography.caption
    )
}

@Composable
fun SwapsCount(swapsCount: Int) {
    Text(
        text = "$swapsCount swaps",
        style = MaterialTheme.typography.caption
    )
}

@Composable
fun MyOutfitsTitle(title: StringResource) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SwapTitle() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.available_swap_title),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )

        SeeAllButton(
            onClick = {}
        )
    }
}

@Composable
fun SearchBar() {
    WhatsInMyClosetTheme {
        TextField(
            value = "SEARCH ...",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text(text = "hint") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                }
            ),
        )
    }
}