package org.greenthread.whatsinmycloset.features.tabs.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.getScreenWidthDp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.profileUser

@Composable
@Preview
fun ProfileTab(onNavigate: (String) -> Unit) {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier
            .fillMaxWidth()
            .padding(50.dp),
            horizontalAlignment = Alignment.Start) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Image
                ProfilePicture()

                Column(Modifier.padding(16.dp)) {
                    // Username
                    Username("Rachel Green")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row() {
                        FriendsCount(12)

                        Spacer(modifier = Modifier.width(16.dp))

                        SwapsCount(10)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfilePicture() {
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