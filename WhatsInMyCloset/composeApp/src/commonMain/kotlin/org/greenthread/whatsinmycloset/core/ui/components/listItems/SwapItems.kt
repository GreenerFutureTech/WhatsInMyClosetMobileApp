package org.greenthread.whatsinmycloset.core.ui.components.listItems

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.theme.onSurfaceLight
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
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onSwapClick() }
                .border(1.dp, onSurfaceLight, RoundedCornerShape(8.dp))
        ) {
            @OptIn(ExperimentalResourceApi::class)
            AsyncImage(
                model = if (loadFailed) Res.getUri("drawable/noImage.png") else imageUrl,
                contentDescription = "Clothing Image",
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(8.dp)),
                onError = { loadFailed = true }
            )
        }
    }
}
@Composable
fun SwapOtherImageCard(onSwapClick: () -> Unit, imageUrl: String, username: String) {
    var loadFailed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .width(125.dp)
            .height(135.dp)
    ) {
        Box(
            modifier = Modifier
                .width(125.dp)
                .height(110.dp)
                .padding(8.dp)
                .clickable { onSwapClick() }
                .border(1.dp, onSurfaceLight, RoundedCornerShape(8.dp))
        ) {
            @OptIn(ExperimentalResourceApi::class)
            AsyncImage(
                model = if (loadFailed) Res.getUri("drawable/noImage.png") else imageUrl,
                contentDescription = "Clothing Image",
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(8.dp)),
                onError = { loadFailed = true }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            @OptIn(ExperimentalResourceApi::class) // TEMP for /drawble image
            (AsyncImage(
                model = Res.getUri("drawable/defaultUser.png"),// NEED TO UPDATE : UserProfileUrl
                contentDescription = "User Image",
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(1.dp, onSurfaceLight, CircleShape)
            ))

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = username, // NEED TO UPDATE : username
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

