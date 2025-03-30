package org.greenthread.whatsinmycloset.features.tabs.home.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.TagsSection
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.greenthread.whatsinmycloset.theme.outlineVariantLight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.item_size
import whatsinmycloset.composeapp.generated.resources.item_name
import whatsinmycloset.composeapp.generated.resources.item_brand

@Composable
fun ItemDetailScreen(
    item: ClothingItem?
) = item?.let {
    WhatsInMyClosetTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .padding(2.dp)
                            .align(Alignment.CenterHorizontally),
                    ) {
                        var imageLoadFailed by remember { mutableStateOf(false) }
                        @OptIn(ExperimentalResourceApi::class)
                        AsyncImage(
                            model = if (imageLoadFailed) Res.getUri("drawable/noImage.png") else item.mediaUrl,
                            contentDescription = "Item Image",
                            modifier = Modifier.fillMaxSize(),
                            onError = { imageLoadFailed = true }
                        )
                    }

                    if (item.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TagsSection(tags = item.tags)
                    }


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {

                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = stringResource(Res.string.item_name),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = item.name ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(top = 12.dp),
                            thickness = 1.dp,
                            color = outlineVariantLight
                        )

                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = stringResource(Res.string.item_brand),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = item.brand ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = outlineVariantLight
                        )

                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = stringResource(Res.string.item_size),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = item.size ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = outlineVariantLight
                        )
                    }
                }
            }
        }
    }
}