package com.nairobi.absensi.dashboard.karyawan

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nairobi.absensi.R
import com.nairobi.absensi.components.Orange
import com.nairobi.absensi.components.Pink
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.successAlert
import com.nairobi.absensi.components.warningAlert
import com.nairobi.absensi.dashboard.DashboardActivity
import com.nairobi.absensi.model.AttendanceViewModel
import com.nairobi.absensi.model.OvertimeViewModel
import com.nairobi.absensi.utils.isToday
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCard(
    onClick: () -> Unit,
    @DrawableRes image: Int,
    color: Color,
    title: String,
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .padding(vertical = 8.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(image),
                    contentScale = ContentScale.Crop,
                )
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            color,
                            Color.Transparent,
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = title,
                    modifier = Modifier
                        .background(
                            Color.White,
                            shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 100))
                        )
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun Home(
    navController: NavController,
    viewModel: OvertimeViewModel,
    viewModel2: AttendanceViewModel
) {
    val context = LocalContext.current

    LaunchedEffect("notification") {
        viewModel.getOvertimes {
            val pending = viewModel.overtimes.value.filter { it.status == "PENDING" }
            if (pending.isNotEmpty()) {
                successAlert(
                    context,
                    "Pemberitahuan",
                    "Kamu memiliki tugas lembur hari ini",
                )
            }
        }
        viewModel2.prepareCheckInCheckOut {
            viewModel2.getAttendances {
                val pending = viewModel2.attendances.value.filter { it.status == "CHECKIN" }
                pending.forEach { attendance ->
                    val closeTime = viewModel2.office.value!!.closeTime
                    val close = Date()
                    close.hours = closeTime.hours
                    if (isToday(attendance.date) && Date().after(close)) {
                        attendance.status = "CHECKOUT"
                        attendance.checkOut = close
                        viewModel2.updateAttendance(attendance)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton(onClick = {
                warningAlert(
                    context,
                    "Logout",
                    "Apakah anda yakin ingin logout?",
                    positiveAction = {
                        val intent = Intent(context, DashboardActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = "Logout",
                )
            }
        }
        Text(
            text = "Selamat Datang",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )
        Text(
            text = "Absensi membutuhkan info lokasi dan verifikasi wajah.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HomeCard(
                onClick = {
                    navController.navigate("checkin")
                },
                image = R.drawable.ic_masuk,
                color = Color.Green,
                title = "Absen Masuk",
            )
            HomeCard(
                onClick = {
                    navController.navigate("checkout")
                },
                image = R.drawable.ic_masuk,
                color = Purple,
                title = "Absen Keluar",
            )
            HomeCard(
                onClick = {
                    navController.navigate("overtime")
                },
                image = R.drawable.ic_lembur,
                color = Color.Blue,
                title = "Absen Lembur",
            )
            HomeCard(
                onClick = {
                    navController.navigate("leave")
                },
                image = R.drawable.ic_izin,
                color = Orange,
                title = "Cuti",
            )
            HomeCard(
                onClick = {
                    navController.navigate("attendance")
                },
                image = R.drawable.ic_history,
                color = Color.Red,
                title = "Riwayat Absensi",
            )
            HomeCard(
                onClick = {
                    navController.navigate("account")
                },
                image = R.drawable.ic_masuk,
                color = Pink,
                title = "Akun",
            )
        }
    }
}