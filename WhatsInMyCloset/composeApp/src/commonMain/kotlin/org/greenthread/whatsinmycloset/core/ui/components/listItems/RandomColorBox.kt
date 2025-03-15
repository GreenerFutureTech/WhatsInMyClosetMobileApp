package org.greenthread.whatsinmycloset.core.ui.components.listItems

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
            .height(50.dp)
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
            contentPadding = PaddingValues(5.dp)
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

@Composable
fun LazyRowColourBox(items: List<ListItem>) {
    if (items.isNotEmpty()) {
        LazyRow(
            Modifier.fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            items(items) { item ->
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(item.height)
                        .padding(4.dp)
                        .background(item.color, shape = RoundedCornerShape(10.dp))
                )
            }
        }
    } else {
        // Show message when list is empty
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No items to display")
        }
    }
}

@Composable
fun LazyGridCalendarUI(items: List<Int>, selectedDay: Int, onDayClick: (Int) -> Unit) {
    if (items.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),  // 7 days in a row
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items) { item ->
                DayCell(
                    day = item,
                    isSelected = item == selectedDay,
                    onClick = { onDayClick(item) }
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No items to display")
        }
    }
}

// **Day Cell UI**
@Composable
fun DayCell(day: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(50.dp) // Ensures rectangular shape
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(4.dp)
            )
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
            .clickable { onClick() }, // Click to select a date
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.White else Color.Black
        )
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
