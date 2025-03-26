import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.CategoriesSection
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.AddSwap.AddSwapViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.greenthread.whatsinmycloset.theme.inversePrimaryLight
import org.greenthread.whatsinmycloset.theme.onSurfaceLight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.viewmodel.koinViewModel
import whatsinmycloset.composeapp.generated.resources.Res

@Composable
fun AddSwapItemRoot(
    viewModel: AddSwapViewModel = koinViewModel(),
    onAddClick: () -> Unit
) {
    val cachedItems by viewModel!!.cachedItems.collectAsState()
    var selectedItems by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedCategory by remember { mutableStateOf<ClothingCategory?>(null) }

    val filteredItems = remember(selectedCategory) {
        if (selectedCategory == null) {
            cachedItems
        } else {
            cachedItems.filter { it.itemType == selectedCategory }
        }
    }

    WhatsInMyClosetTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Selected ${selectedItems.size} Item(s)",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = {
                        val selectedItemIds = selectedItems.toList()
                        viewModel.createSwap(selectedItemIds)
                        onAddClick()
                    },
                    enabled = selectedItems.isNotEmpty()
                ) {
                    Text("Add")
                }
            }

            CategoriesSection { category ->
                selectedCategory = category
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredItems) { item ->
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