package com.nairobi.absensi.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.components.FormEmail
import com.nairobi.absensi.components.FormPassword
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.dashboard.DashboardActivity
import com.nairobi.absensi.model.UserViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: UserViewModel) {
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_login),
                contentScale = ContentScale.FillBounds,
            )
    ) {
        LoginContent(navController, viewModel)
        FloatingActionButton(
            onClick = {
                val walink = "https://wa.link/wwgcfo"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(walink)
                startActivity(context, intent, null)
            },
            containerColor = Color.White,
            contentColor = Purple,
            shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 100)),
            modifier = Modifier
                .padding(16.dp),
        ) {
            Icon(
                painterResource(id = R.drawable.ic_wa),
                contentDescription = "Whatsapp",
            )
        }
    }
}

@Composable
fun LoginContent(navController: NavController, viewModel: UserViewModel) {
    val activity = LocalContext.current as AuthActivity
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    fun login() {
        if (email.isEmpty()) {
            emailError = true
        }
        if (password.isEmpty()) {
            passwordError = true
        }
        if (!emailError && !passwordError) {
            val loading = loadingAlert(activity)
            viewModel.getUserByEmail(email) {
                if (viewModel.error.value != null || viewModel.user.value == null) {
                    // Show alert dialog
                    loading.dismissWithAnimation()
                    errorAlert(
                        activity,
                        title = "Login gagal",
                        text = "Username atau password salah",
                    )
                } else {
                    if (viewModel.user.value!!.password == password) {
                        loading.dismissWithAnimation()
                        val intent = Intent(activity, DashboardActivity::class.java)
                        intent.putExtra("userId", viewModel.user.value!!.id)
                        startActivity(activity, intent, null)
                        activity.finish()
                    } else {
                        loading.dismissWithAnimation()
                        errorAlert(
                            activity,
                            title = "Login gagal",
                            text = "Username atau password salah",
                        )
                    }
                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
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
                        text = "Toko sparepart motor anji",
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
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple,
                            contentColor = Color.White,
                        ),
                        onClick = {
                            login()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(text = "Masuk")
                    }
                    Text(
                        text = "Lupa password?",
                        color = Purple,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clickable {
                                navController.navigate("forgot-password")
                            }
                    )
                }
            }
            Text(
                text = "Hubungi petugas untuk\nmengetahui password anda",
                color = Purple,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}