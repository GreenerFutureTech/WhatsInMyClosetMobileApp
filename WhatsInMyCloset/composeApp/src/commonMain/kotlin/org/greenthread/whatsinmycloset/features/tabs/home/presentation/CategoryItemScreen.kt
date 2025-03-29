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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import whatsinmycloset.composeapp.generated.resources.no_item_category
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.ui.components.controls.SearchBar
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.CategoryItemsViewModel
import whatsinmycloset.composeapp.generated.resources.Res
import org.greenthread.whatsinmycloset.theme.onSurfaceLight
import org.greenthread.whatsinmycloset.theme.secondaryLight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoryItemScreen(
    categoryName: String,
    viewModel: CategoryItemsViewModel = koinViewModel(),
    onItemClick: (ClothingItem) -> Unit
) {

    var searchString by remember { mutableStateOf("") }

    val category = remember(categoryName) {
        if (categoryName != "All") {
            ClothingCategory.valueOf(categoryName)
        } else {
            null
        }
    }

    LaunchedEffect(category) {
        if (category != null) {
            viewModel.loadItemsByCategory(category)
        }
    }
    val items by if (categoryName == "All") {
        viewModel.cachedItems.collectAsState()
    } else {
        viewModel.categoryItems.collectAsState()
    }

    val filteredItems = items.filter { item ->
        val query = searchString.lowercase()
        item.brand?.contains(searchString, ignoreCase = true) == true ||
                item.name.contains(query, ignoreCase = true) ||
                item.tags.any { tag -> tag.contains(searchString, ignoreCase = true) }

    }

    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = category?.name?:"All",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            items.isEmpty() -> {
                Text(
                    text = stringResource(Res.string.no_item_category),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                SearchBar(
                    searchString = searchString,
                    onSearchStringChange = { searchString = it },
                    onSearch = {},
                    modifier = Modifier.fillMaxWidth()
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredItems) { item ->
                        CategoryItemCard(
                            imageUrl = item.mediaUrl ?: "",
                            onItemClick = {onItemClick(item)}
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryItemCard(
    imageUrl: String,
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
                    width = 1.dp,
                    color = secondaryLight,
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