package com.nairobi.absensi.dashboard.admin

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nairobi.absensi.components.Orange
import com.nairobi.absensi.components.Pink
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.successAlert
import com.nairobi.absensi.components.warningAlert
import com.nairobi.absensi.dashboard.DashboardActivity
import com.nairobi.absensi.model.LeaveViewModel

@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
    title: String = "",
    icon: ImageVector? = null,
    color: Color = Color.Red,
    onClick: () -> Unit = {},
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .padding(horizontal = 10.dp)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Icon(
                icon ?: Icons.Default.Settings,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .background(
                        color,
                        shape = CircleShape,
                    )
                    .padding(10.dp)
                    .size(50.dp)
            )
            Text(
                title,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color,
                        shape = CircleShape,
                    )
            )
            Text(
                title,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun Home(navController: NavController, viewModel: LeaveViewModel) {
    val context = LocalContext.current
    val modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .padding(top = 20.dp)

    LaunchedEffect("notification") {
        viewModel.getLeaves {
            val pending = viewModel.leaves.value.filter { it.status == "PENDING" }.size
            if (pending != 0) {
                successAlert(
                    context,
                    "Pemberitahuan",
                    "Anda memiliki $pending permintaan cuti",
                )
            }
        }
    }

    Column(
        Modifier
            .background(Color.White)
            .padding(20.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier) {
            HomeCard(
                title = "Admin",
                icon = Icons.Default.Settings,
                color = Color.Red,
                onClick = {
                    navController.navigate("admin")
                },
                modifier = Modifier
                    .weight(1f)
            )
            HomeCard(
                title = "Karyawan",
                icon = Icons.Default.Person,
                color = Color.Blue,
                onClick = {
                    navController.navigate("user")
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
        Row(modifier) {
            HomeCard(
                title = "Absensi",
                icon = Icons.Default.DateRange,
                color = Color.Green,
                onClick = {
                    navController.navigate("attendance")
                },
                modifier = Modifier
                    .weight(1f)
            )
            HomeCard(
                title = "Lembur",
                icon = Icons.Default.Bedtime,
                color = Orange,
                onClick = {
                    navController.navigate("overtime")
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
        Row(modifier) {
            HomeCard(
                title = "Cuti",
                icon = Icons.Default.AddTask,
                color = Purple,
                onClick = {
                    navController.navigate("leave")
                },
                modifier = Modifier
                    .weight(1f)
            )
            HomeCard(
                title = "Kantor",
                icon = Icons.Default.Domain,
                color = Color.Cyan,
                onClick = {
                    navController.navigate("office")
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
        Row(modifier.padding(bottom = 20.dp)) {
            HomeCard(
                title = "Export",
                icon = Icons.Default.TableView,
                color = Pink,
                onClick = {
                    navController.navigate("exports")
                },
                modifier = Modifier
                    .weight(1f)
            )
            HomeCard(
                title = "Keluar",
                icon = Icons.Default.Logout,
                color = Color.Gray,
                onClick = {
                    warningAlert(
                        context,
                        "Keluar",
                        "Apakah anda yakin ingin keluar?",
                        positiveAction = {
                            val intent = Intent(context, DashboardActivity::class.java)
                            context.startActivity(intent)
                            (context as DashboardAdminActivity).finish()
                        }
                    )
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}