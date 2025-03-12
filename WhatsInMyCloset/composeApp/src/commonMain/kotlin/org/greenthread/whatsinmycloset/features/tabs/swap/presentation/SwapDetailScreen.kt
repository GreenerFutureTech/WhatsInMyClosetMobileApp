package org.greenthread.whatsinmycloset.features.tabs.swap.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import whatsinmycloset.composeapp.generated.resources.Res

@Composable
fun SwapDetailScreen(
    swap: SwapDto?,
    onBackClick: () -> Unit,
    userUser: User?
) = swap?.let {
    val currentUser = userUser?:return

    WhatsInMyClosetTheme {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Back")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()

        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .align(Alignment.Center)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    @OptIn(ExperimentalResourceApi::class) // TEMP for /drawble image
                    AsyncImage(
                        model = Res.getUri("drawable/defaultUser.png"), // NEED TO UPDATE : UserProfileUrl
                        contentDescription = "User Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray, CircleShape)
                            .padding(2.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "user${swap.userId}",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                        .padding(2.dp)
                        .align(Alignment.CenterHorizontally),
                ) {
                    @OptIn(ExperimentalResourceApi::class) // TEMP for /drawble image
                    AsyncImage(
                        model = Res.getUri("drawable/default.png"),
                        contentDescription = "Swap Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = swap.brand,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Size: ${swap.size}",
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Condition: ${swap.condition}",
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    if (swap.userId != currentUser.id) {
                        Button(
                            onClick = { /* TODO: Implement Swap Request action */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Swap Request",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
} ?: Text("No swap data available")

