package org.greenthread.whatsinmycloset.features.tabs.social.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SocialTabScreen(
    user: User?,
    onNavigate: (String) -> Unit,
    navController: NavController,
    viewModel: PostViewModel = koinViewModel(),
) {
    val currentUser = viewModel.currentUser
    var showContent by remember { mutableStateOf(false) }
    Scaffold {
        PostsGrid(
            viewModel = viewModel,
            navController = navController,
            onPostClick = { navController.navigate(Routes.SocialDetailsScreen) },
            modifier = Modifier
        )
    }
}