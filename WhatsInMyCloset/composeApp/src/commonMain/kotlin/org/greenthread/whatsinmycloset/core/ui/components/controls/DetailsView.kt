package org.greenthread.whatsinmycloset.core.ui.components.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.greenthread.whatsinmycloset.core.utilities.AppConstants

@Composable
fun DetailsView(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .clickable {

            }
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Details")

        Text(
            text = "Back",
            modifier = Modifier.clickable {
                onNavigate(AppConstants.BACK_CLICK_ROUTE.toString())
            }
        )
    }
}