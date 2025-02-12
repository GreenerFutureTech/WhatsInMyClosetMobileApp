package org.greenthread.whatsinmycloset.features.tabs.swap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.greenthread.whatsinmycloset.core.ui.components.controls.Greeting
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun SwapTab(onNavigate: (String) -> Unit) {
    MaterialTheme {
        SwapScreenRoot()
    }
}

@Composable
fun SwapScreenRoot() {
    Column {
        //Spacer(modifier = Modifier.height(60.dp))

        Row(  modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {

            androidx.compose.material3.Text(modifier = Modifier.height(48.dp), fontSize = 30.sp, text = "SWAP")

            Icon(
                imageVector = Icons.Default.MailOutline,
                contentDescription = "Messages", // Accessibility description
                modifier = Modifier.size(48.dp), // Set icon size
                tint = MaterialTheme.colors.primary // Use theme color for the icon
            )

        }

        Row(  modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {

            androidx.compose.material3.Text(
                modifier = Modifier.height(25.dp),
                fontSize = 25.sp,
                text = "My Swap Items")

            TextButton(
                onClick = {
                    println("Button clicked!")
                },
                modifier = Modifier.height(50.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "All Swaps",
                    fontSize = 15.sp,
                    color = Color.Blue
                )
            }
        }

        val myRandomSwaps = generateRandomItems(2)
        LazyGridColourBox(items = myRandomSwaps)

        androidx.compose.material3.Text(
            modifier = Modifier.height(30.dp).fillMaxSize(),
            fontSize = 25.sp,
            text = "Followers and Nearby Items"
        )

        TextField(
            value = "SEARCH ...",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { androidx.compose.material3.Text(text = "hint") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                }
            ),
        )

        val otherRandomSwaps = generateRandomItems(6)
        LazyGridColourBox(items = otherRandomSwaps)

    }

}


@Composable
fun SwapItemScreen() {
    Box(
        modifier = Modifier.fillMaxSize(), // Fill the entire available space
        contentAlignment = Alignment.Center // Center the content inside the Box
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center), // Center the Column inside the Box
            horizontalAlignment = Alignment.CenterHorizontally // Center children horizontally
        ) {
            val randomItems = generateRandomItems(1) // Generate 10 random items for the preview
            LazyGridColourBox(items = randomItems)

            androidx.compose.material3.Text(
                modifier = Modifier.height(25.dp),
                fontSize = 25.sp,
                text = "BRAND"
            )

            androidx.compose.material3.Text(
                modifier = Modifier.height(25.dp),
                fontSize = 25.sp,
                text = "SIZE"
            )

            androidx.compose.material3.Text(
                modifier = Modifier.height(25.dp),
                fontSize = 25.sp,
                text = "CONDITION"
            )

            androidx.compose.material3.Text(
                modifier = Modifier.height(25.dp),
                fontSize = 25.sp,
                text = "LOCATION"
            )

            androidx.compose.material3.Button(
                onClick = {}
            ) {
                androidx.compose.material3.Text(
                    modifier = Modifier.height(25.dp),
                    fontSize = 25.sp,
                    text = "Swap Request"
                )
            }
        }
    }
}