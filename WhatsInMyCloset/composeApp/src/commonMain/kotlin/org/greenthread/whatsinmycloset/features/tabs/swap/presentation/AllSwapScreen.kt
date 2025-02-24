import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.ui.components.listItems.SwapImageCard
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AllSwapsScreen(
    navController: NavController,
    viewModel: SwapViewModel = koinViewModel(),
    onSwapClick: (SwapDto) -> Unit,
    ) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = SwapListState(),
        lifecycle = lifecycle
    )

    LaunchedEffect(Unit) {
        if (state.getUserSwapResults.isEmpty()) {
            viewModel.fetchSwapData("1") // NEED TO UPDATE : current user id
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {

        TextButton(onClick = { navController.popBackStack() }) {
            Text(
                text = "Back",
                fontSize = 15.sp,
                color = Color.Blue
            )
        }

        Text(
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            text = "All Swap Items"
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(state.getUserSwapResults) { _, item ->
                SwapImageCard(
                    onSwapClick = {
                        onSwapClick(item)
                    }
                )
            }
        }
    }
}
