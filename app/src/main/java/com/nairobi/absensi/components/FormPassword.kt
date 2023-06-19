package com.nairobi.absensi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

fun passwordValidator(password: String): Boolean {
    return password.length >= 6
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPassword(
    modifier: Modifier = Modifier,
    password: String = "",
    passwordError: Boolean = false,
    disabled: Boolean = false,
    onPasswordChange: (String) -> Unit = {},
    onPasswordErrorChange: (Boolean) -> Unit = {},
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = {
            onPasswordChange(it)
            onPasswordErrorChange(!passwordValidator(it))
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
        isError = passwordError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        label = { Text(text = "Password") },
        leadingIcon = {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Password",
            )
        },
        trailingIcon = {
            Icon(
                if (!showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Password",
                modifier = Modifier.clickable {
                    showPassword = !showPassword
                }
            )
        },
        supportingText = {
            if (passwordError) {
                Text(
                    text = "Password minimal 6 karakter",
                    color = Color.Red,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                )
            }
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        readOnly = disabled,
        modifier = modifier,
    )
}