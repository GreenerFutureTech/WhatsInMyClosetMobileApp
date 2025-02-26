package org.greenthread.whatsinmycloset.core.ui.components.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar(
    searchString: String,
    onSearchStringChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
){
    val color = Color(0xFFF2E1D0)

    OutlinedTextField(
        value = searchString,
        onValueChange = onSearchStringChange,
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.Gray
        ),
        textStyle = TextStyle(fontSize = 20.sp),
        placeholder = {
            Text("Search..")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)

            )
        },
        singleLine = true,
        keyboardActions = KeyboardActions (
            onSearch = {
                onSearch()
            }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        trailingIcon = {
            AnimatedVisibility(
                visible = searchString.isNotBlank()
            ) {
                IconButton(
                    onClick = {
                        onSearchStringChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(50))
            .border(2.dp, color, shape = RoundedCornerShape(50))
            .minimumInteractiveComponentSize()
    )

}