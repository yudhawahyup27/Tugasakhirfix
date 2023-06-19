package com.nairobi.absensi.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

fun emailValidator(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormEmail(
    modifier: Modifier = Modifier,
    email: String = "",
    emailError: Boolean = false,
    disabled: Boolean = false,
    onEmailChange: (String) -> Unit = {},
    onEmailErrorChange: (Boolean) -> Unit = {},
) {
    OutlinedTextField(
        value = email,
        onValueChange = {
            onEmailChange(it)
            onEmailErrorChange(!emailValidator(it))
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
        isError = emailError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        label = { Text(text = "Email") },
        leadingIcon = {
            Icon(
                Icons.Default.Person,
                contentDescription = "Email",
            )
        },
        supportingText = {
            if (emailError) {
                Text(
                    text = "Email tidak valid",
                    color = Color.Red,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                )
            }
        },
        readOnly = disabled,
        modifier = modifier,
    )
}