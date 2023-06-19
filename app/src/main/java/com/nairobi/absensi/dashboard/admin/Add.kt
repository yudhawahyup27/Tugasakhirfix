package com.nairobi.absensi.dashboard.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.nairobi.absensi.components.UserForm
import com.nairobi.absensi.model.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Add(navController: NavController, userViewModel: UserViewModel) {
    UserForm(navController, userViewModel, true)
}