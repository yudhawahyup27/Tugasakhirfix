package com.nairobi.absensi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nairobi.absensi.auth.AuthActivity
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartupActivity : ComponentActivity() {
    private var savedIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }
        val ref = getSharedPreferences(this.packageName, MODE_PRIVATE)
        val userId = ref.getString("userId", null)
        startApp(userId)
    }

    private fun startApp(userId: String?) {
        savedIntent = if (userId != null) {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("userId", userId)
            intent
        } else {
            val intent = Intent(this, AuthActivity::class.java)
            intent
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun SplashScreen() {
        val systemUiController: SystemUiController = rememberSystemUiController()
        systemUiController.isStatusBarVisible = false
        systemUiController.isNavigationBarVisible = false

        val permissionState = rememberMultiplePermissionsState(
            listOf(
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )

        var hasLaunchedPermissionRequest by remember { mutableStateOf(false) }

        val appVersion = BuildConfig.VERSION_NAME
        val appCode = BuildConfig.VERSION_CODE
        val version = "v$appVersion build $appCode"

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splashscreen),
                    contentDescription = "Splash Screen",
                    modifier = Modifier
                        .size(125.dp)
                        .background(color = Color.Transparent)
                )
                Text(
                    text = "Absensi",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Gray
                )
            }
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp)
            ) {
                Text(text = version)
            }
            if (!permissionState.allPermissionsGranted) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black.copy(alpha = 0.5f))
                        .padding(20.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                        ) {
                            Text("Aplikasi ini membutuhkan beberapa izin untuk dapat berjalan dengan baik. Silahkan berikan izin yang dibutuhkan.")
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(top = 20.dp)
                                    .fillMaxWidth(),
                            ) {
                                Button(
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = Purple,
                                        contentColor = Color.White,
                                    ),
                                    onClick = {
                                        if (!hasLaunchedPermissionRequest) {
                                            hasLaunchedPermissionRequest = true
                                            permissionState.launchMultiplePermissionRequest()
                                        } else {
                                            // open app settings
                                            val intent =
                                                Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            intent.data =
                                                android.net.Uri.parse("package:$packageName")
                                            startActivity(intent)
                                        }
                                    }) {
                                    Text("Berikan izin")
                                }
                            }
                        }
                    }
                }
            } else {
                startActivity(savedIntent)
                finish()
            }
        }
    }
}