package com.nairobi.absensi.dashboard.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.ErrorScreen
import com.nairobi.absensi.components.FabAdd
import com.nairobi.absensi.components.FormText
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.data.User
import com.nairobi.absensi.model.UserViewModel

@Composable
fun Officer(navController: NavController, userViewModel: UserViewModel) {
    userViewModel.refreshUsers()

    BackHandler {
        navController.popBackStack()
    }
    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route != "add" && dest.route != "edit" && dest.route != "user") {
            userViewModel.clean()
        }
    }

    if (userViewModel.loading.value) {
        ProgressScreen()
    } else if (userViewModel.error.value != null) {
        ErrorScreen(userViewModel.error.value!!)
    } else {
        var filter by remember { mutableStateOf<String?>(null) }
        val users = userViewModel.users.value
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                AppBar(navController = navController, title = "Karyawan")
                FormText(
                    text = filter ?: "",
                    label = "Cari Karyawan",
                    onTextChange = { filter = it },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Cari",
                        )
                    },
                    trailingIcon = {
                        if (filter != null) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Hapus",
                                modifier = Modifier
                                    .clickable {
                                        filter = null
                                    }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    users
                        .filter { user -> user.role == "KARYAWAN" }
                        .filter { user ->
                            filter == null || user.name.contains(
                                filter!!,
                                ignoreCase = true
                            ) || user.email.contains(
                                filter!!,
                                ignoreCase = true
                            ) || user.nip?.contains(filter!!, ignoreCase = true) == true
                        }
                        .forEach { user ->
                            Card(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp,
                                    pressedElevation = 4.dp,
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black,
                                ),
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        userViewModel.tempUser.value = user
                                        navController.navigate("edit")
                                    }
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        user.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        user.email,
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                    )
                                    if (user.role == "KARYAWAN") {
                                        Text(
                                            user.phone ?: "",
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                        )
                                        Text(
                                            user.nip ?: "",
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                        )
                                    }
                                }
                            }
                        }
                }
            }
            FabAdd {
                val user = User(role = "KARYAWAN")
                userViewModel.tempUser.value = user
                navController.navigate("add")
            }
        }
    }
}