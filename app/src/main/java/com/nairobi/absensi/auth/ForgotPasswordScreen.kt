package com.nairobi.absensi.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.components.ErrorScreen
import com.nairobi.absensi.components.FormEmail
import com.nairobi.absensi.components.FormPassword
import com.nairobi.absensi.components.FormText
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.model.UserViewModel

@Composable
fun ForgotPasswordScreen(navController: NavController, userViewModel: UserViewModel) {
    userViewModel.refreshUsers()

    if (userViewModel.loading.value) {
        ProgressScreen()
    } else if (userViewModel.error.value != null) {
        ErrorScreen(userViewModel.error.value!!)
    } else {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(id = R.drawable.bg_login),
                    contentScale = ContentScale.FillBounds,
                )
        ) {
            ForgotPasswordContent(navController, userViewModel)
        }
    }
}

enum class ForgotPasswordState {
    Email,
    Password,
}

@Composable
fun ForgotPasswordContent(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var emailDisabled by remember { mutableStateOf(false) }
    var nip by remember { mutableStateOf("") }
    var nipError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var forgotState by remember { mutableStateOf(ForgotPasswordState.Email) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp,
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Lupa password",
                    color = Purple,
                    style = MaterialTheme.typography.headlineSmall,
                    lineHeight = 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                FormEmail(
                    email = email,
                    emailError = emailError,
                    onEmailChange = { email = it },
                    onEmailErrorChange = { emailError = it },
                    disabled = emailDisabled,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (forgotState == ForgotPasswordState.Password) {
                    FormText(
                        text = nip,
                        textError = nipError,
                        onTextChange = { nip = it },
                        onTextErrorChange = { nipError = it },
                        label = "NIP",
                        validator = { it.length == 6 },
                        supportingText = "NIP harus 6 digit",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Key,
                                contentDescription = "NIP",
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    FormPassword(
                        password = password,
                        passwordError = passwordError,
                        onPasswordChange = { password = it },
                        onPasswordErrorChange = { passwordError = it },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple,
                        contentColor = Color.White,
                    ),
                    onClick = {
                        when (forgotState) {
                            ForgotPasswordState.Email -> {
                                if (email.isEmpty()) {
                                    emailError = true
                                    return@Button
                                }
                                val user = userViewModel.users.value.find { it.email == email }
                                if (user != null) {
                                    emailDisabled = true
                                    forgotState = ForgotPasswordState.Password
                                    userViewModel.tempUser.value = user
                                } else {
                                    errorAlert(
                                        context,
                                        "Error",
                                        "User tidak ditemukan",
                                    )
                                }
                            }

                            ForgotPasswordState.Password -> {
                                if (nip.isEmpty()) {
                                    nipError = true
                                    return@Button
                                }
                                if (password.isEmpty()) {
                                    passwordError = true
                                    return@Button
                                }
                                val user = userViewModel.tempUser.value!!
                                if (user.nip != nip) {
                                    errorAlert(
                                        context,
                                        "Error",
                                        "NIP tidak sesuai",
                                    )
                                    return@Button
                                } else {
                                    val loading = loadingAlert(context)
                                    userViewModel.updateUser(
                                        context,
                                        user.copy(password = password)
                                    ) {
                                        loading.dismissWithAnimation()
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    val text = when (forgotState) {
                        ForgotPasswordState.Email -> "Cari akun"
                        ForgotPasswordState.Password -> "Ganti password"
                    }
                    Text(text)
                }
                Text(
                    text = "Batal",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
            }
        }
    }
}