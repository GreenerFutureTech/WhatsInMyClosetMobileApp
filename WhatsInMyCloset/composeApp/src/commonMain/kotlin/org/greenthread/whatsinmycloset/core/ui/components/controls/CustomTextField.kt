package org.greenthread.whatsinmycloset.core.ui.components.controls

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(remember { FocusRequester() }),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        label = {
            Text(
                text = label,
                fontSize = 14.sp
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.LightGray,
            focusedBorderColor = Color.LightGray,
            unfocusedBorderColor = Color.LightGray
        ),
        visualTransformation = if (keyboardOptions.keyboardType == KeyboardType.Password)
            PasswordVisualTransformation()
        else
            VisualTransformation.None
    )
}
