package org.greenthread.whatsinmycloset.core.ui.components.listItems

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.core.dto.MessageUserDto
import org.greenthread.whatsinmycloset.theme.onSurfaceLight
import org.greenthread.whatsinmycloset.theme.secondaryLight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import whatsinmycloset.composeapp.generated.resources.Res


@Composable
fun SwapImageCard(
    onSwapClick: () -> Unit,
    imageUrl: String
) {
    var loadFailed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .width(125.dp)
            .height(110.dp)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onSwapClick() }
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
        ) {
            @OptIn(ExperimentalResourceApi::class)
            AsyncImage(
                model = if (loadFailed) Res.getUri("drawable/noImage.png") else imageUrl,
                contentDescription = "Clothing Image",
                modifier = Modifier
                    .fillMaxSize(0.95f)
                    .clip(RoundedCornerShape(8.dp)),
                onError = { loadFailed = true }
            )
        }
    }
}

@Composable
fun SwapOtherImageCard(onSwapClick: () -> Unit, imageUrl: String, user: MessageUserDto) {
    Column(
        modifier = Modifier
            .width(125.dp)
            .height(160.dp) // Increase height to accommodate user info
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween, // Ensures spacing between items
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Swap Card
        SwapImageCard(
            onSwapClick = onSwapClick,
            imageUrl = imageUrl
        )

        Spacer(modifier = Modifier.height(8.dp)) // Ensures space between SwapImageCard and user info

        // User Profile Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var loadFailed by remember { mutableStateOf(false) }
            @OptIn(ExperimentalResourceApi::class)
            AsyncImage(
                model = if (loadFailed) Res.getUri("drawable/defaultUser.png") else user.profilePicture,
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(28.dp) // Slightly larger for visibility
                    .clip(CircleShape)
                    .border(1.dp, secondaryLight, CircleShape),
                onError = { loadFailed = true }
            )
            Spacer(modifier = Modifier.width(8.dp))

            user.username?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f) // Helps ensure text does not get cut off
                )
            }
        }
    }
}


