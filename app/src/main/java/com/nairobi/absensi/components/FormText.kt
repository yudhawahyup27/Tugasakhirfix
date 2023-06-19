package com.nairobi.absensi.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormText(
    modifier: Modifier = Modifier,
    text: String = "",
    textError: Boolean = false,
    disabled: Boolean = false,
    onTextChange: (String) -> Unit = {},
    onTextErrorChange: (Boolean) -> Unit = {},
    label: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: String = "",
    isNumber: Boolean = false,
    wrap: Boolean = false,
    validator: (String) -> Boolean = { true },
) {
    OutlinedTextField(
        value = text,
        onValueChange = {
            onTextChange(it)
            onTextErrorChange(!validator(it))
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            unfocusedLeadingIconColor = Color.Black,
            unfocusedTrailingIconColor = Color.Black,
            focusedBorderColor = Purple,
            focusedLabelColor = Purple,
            focusedLeadingIconColor = Purple,
            focusedTrailingIconColor = Purple,
            errorBorderColor = Color.Red,
            errorLabelColor = Color.Red,
            errorLeadingIconColor = Color.Red,
            errorTrailingIconColor = Color.Red,
            disabledBorderColor = Color.Black,
            disabledLabelColor = Color.Black,
            disabledLeadingIconColor = Color.Black,
            disabledTrailingIconColor = Color.Black,
            disabledTextColor = Color.Black,
        ),
        isError = textError,
        singleLine = !wrap,
        label = { Text(text = label) },
        leadingIcon = {
            if (leadingIcon != null) {
                leadingIcon()
            }
        },
        trailingIcon = {
            if (trailingIcon != null) {
                trailingIcon()
            }
        },
        supportingText = {
            if (textError) {
                Text(
                    text = supportingText,
                    color = Color.Red,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                )
            }
        },
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        readOnly = disabled,
        enabled = !disabled,
        maxLines = if (wrap) Int.MAX_VALUE else 1,
        modifier = modifier,
    )
}