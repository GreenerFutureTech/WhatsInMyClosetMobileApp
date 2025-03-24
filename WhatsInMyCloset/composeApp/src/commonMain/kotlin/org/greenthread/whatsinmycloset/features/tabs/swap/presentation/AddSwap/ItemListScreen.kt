import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.AddSwapViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.greenthread.whatsinmycloset.theme.inversePrimaryLight
import org.greenthread.whatsinmycloset.theme.onSurfaceLight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.viewmodel.koinViewModel
import whatsinmycloset.composeapp.generated.resources.Res

@Composable
fun AddSwapItemRoot(
    viewModel: AddSwapViewModel = koinViewModel(),
) {
    var selectedItems by remember { mutableStateOf<Set<String>>(emptySet()) }

    WhatsInMyClosetTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selected ${selectedItems.size} Item(s)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sampleSwapItem()) { item ->
                    ItemImageCard(
                        imageUrl = item.mediaUrl ?: "",
                        isSelected = selectedItems.contains(item.id),
                        onItemClick = {
                            selectedItems = if (selectedItems.contains(item.id)) {
                                selectedItems - item.id
                            } else {
                                selectedItems + item.id
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemImageCard(
    imageUrl: String,
    isSelected: Boolean,
    onItemClick: () -> Unit
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
                .clickable { onItemClick() }
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) inversePrimaryLight else onSurfaceLight,
                    shape = RoundedCornerShape(8.dp)
                )
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


// TEMP
fun sampleSwapItem(): List<ClothingItem> {
    return listOf(
        ClothingItem(
            id = "c9f8af7e-9b70-47e1-92be-6457bfcf6325",
            name = "Cool Pants",
            wardrobeId = "f676c47d-d1cc-4128-9bb3-1f9f164022b2",
            itemType = ClothingCategory.BOTTOMS,
            mediaUrl = "https://greenthreaditems.blob.core.windows.net/images/test_pants.png",
            tags = listOf("casual", "summer"),
            condition = "Like New",
            brand = "Nike",
            size = "Small"
        ),
        ClothingItem(
            id = "84baae93-b65e-4eab-9b53-5ad190d0f150",
            name = "Cool Jacket",
            itemType = ClothingCategory.TOPS,
            mediaUrl = "https://greenthreaditems.blob.core.windows.net/images/test_shirt2.png",
            tags = listOf("casual", "summer"),
            condition = "Like New",
            brand = "Gap",
            size = "Small"
        )
    )
}