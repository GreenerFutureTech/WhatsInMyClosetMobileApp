package org.greenthread.whatsinmycloset.core.ui.components.listItems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

//A test class
//Contains the ListItem which is used to create a RandomColourBox.
//
//Then, the LazyGridColourBox accepts the below code as its "child"
//
/*{
    items(items) { item ->
        RandomColourBox(item = item)
    }
}*/
//which causes a RandomColourBox to be created for each item in the items list.

data class ListItem(
    val height: Dp,
    val color: Color
)

@Composable
fun RandomColourBox(item: ListItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(item.height)
            .padding(4.dp)
            .background(item.color, shape = RoundedCornerShape(10.dp))
    )
}

@Composable
fun LazyGridColourBox(items: List<ListItem>) {
    if (items.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(32.dp)
        ) {
            items(items) { item ->
                RandomColourBox(item = item)
            }
        }
    } else {
        // Handle empty list case (optional)
        Box(modifier = Modifier.fillMaxSize()) {
            Text("No items to display")
        }
    }
}

fun generateRandomItems(count: Int): List<ListItem> {
    return List(count) {
        ListItem(
            height = (125).dp,
            color = Color(
                red = Random.nextFloat(),
                green = Random.nextFloat(),
                blue = Random.nextFloat(),
                alpha = 1f // Fully opaque
            )
        )
    }
}
